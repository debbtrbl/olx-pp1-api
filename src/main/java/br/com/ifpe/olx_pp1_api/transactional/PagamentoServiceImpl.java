package br.com.ifpe.olx_pp1_api.transactional;

import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.service.ProdutoService;
import br.com.ifpe.olx_pp1_api.service.UsuarioService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    @Value("${stripe.api.key:}")
    private String stripeApiKey;

    public PagamentoServiceImpl(PagamentoRepository pagamentoRepository,
                                ProdutoService produtoService,
                                UsuarioService usuarioService) {
        this.pagamentoRepository = pagamentoRepository;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    @Override
    public String getStripeApiKey() {
        return stripeApiKey;
    }

    @Override
    @Transactional
    public Pagamento iniciarPagamento(Long produtoId, Long compradorId, Long unitAmountCents, Long quantity, String successUrl, String cancelUrl) throws Exception {
        if (stripeApiKey == null || stripeApiKey.isBlank()) {
            throw new IllegalStateException("Chave da Stripe não configurada (stripe.api.key)");
        }

        // buscar produto e usuario (lançando se não existir)
        Produto produto = produtoService.visualizarDetalhes(produtoId);
        Usuario comprador = usuarioService.buscarPorId(compradorId);

        if (unitAmountCents == null || unitAmountCents <= 0) {
            throw new IllegalArgumentException("unitAmountCents inválido");
        }
        if (quantity == null || quantity <= 0) quantity = 1L;

        // cria Pagamento PENDENTE
        Pagamento pagamento = Pagamento.builder()
                .produto(produto)
                .comprador(comprador)
                .status(StatusPagamento.PENDENTE)
                .amountCents(unitAmountCents)
                .quantity(quantity)
                .build();

        pagamento = pagamentoRepository.save(pagamento);

        // cria sessão no Stripe
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(produto.getNome() != null ? produto.getNome() : "Produto")
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("brl")
                        .setUnitAmount(unitAmountCents)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem item =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(quantity)
                        .setPriceData(priceData)
                        .build();

        String sUrl = successUrl != null ? ensurePort(successUrl, 8081) : "http://localhost:8081/sucesso";
        String cUrl = cancelUrl != null ? ensurePort(cancelUrl, 8081) : "http://localhost:8081/erro";

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(item)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // passamos o id do pagamento no metadata para achar depois
                .putAllMetadata(Map.of("pagamentoId", String.valueOf(pagamento.getId()),
                                       "produtoId", String.valueOf(produtoId),
                                       "compradorId", String.valueOf(compradorId)))
                .setSuccessUrl(sUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cUrl)
                .build();

        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            // marcar pagamento como FALHOU
            pagamento.setStatus(StatusPagamento.FALHOU);
            pagamentoRepository.save(pagamento);
            throw e;
        }

        // salvar sessionId no pagamento
        pagamento.setStripeSessionId(session.getId());
        pagamento.setCheckoutUrl(session.getUrl());
        pagamento = pagamentoRepository.save(pagamento);

        return pagamento;
    }

    private String ensurePort(String url, int port) {
        if (url == null) return null;
        if (url.startsWith("/")) return "http://localhost:" + port + url;
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
            if (host == null) {
                return "http://localhost:" + port + (url.startsWith("/") ? url : "/" + url);
            }
            URI withPort = new URI(scheme, uri.getUserInfo(), host, port, uri.getPath(), uri.getQuery(), uri.getFragment());
            return withPort.toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }

    @Override
    @Transactional
    public void processarSessaoStripeCompletada(String stripeSessionId) throws Exception {
        System.out.println("[DEBUG-PROCESS] Iniciando processarSessaoStripeCompletada para: " + stripeSessionId);
        
        Optional<Pagamento> op = pagamentoRepository.findByStripeSessionId(stripeSessionId);
        if (op.isEmpty()) {
            System.out.println("[ERROR-PROCESS] Pagamento não encontrado para sessionId: " + stripeSessionId);
            throw new IllegalArgumentException("Pagamento com stripeSessionId não encontrado: " + stripeSessionId);
        }

        Pagamento pagamento = op.get();
        System.out.println("[DEBUG-PROCESS] Pagamento encontrado. ID: " + pagamento.getId() + ", Status atual: " + pagamento.getStatus());

        // se já estiver aprovado, ignora
        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            System.out.println("[DEBUG-PROCESS] Pagamento já está APROVADO, ignorando");
            return;
        }

        // marca como aprovado
        System.out.println("[DEBUG-PROCESS] Marcando pagamento como APROVADO...");
        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento.setDataConfirmacao(java.time.LocalDateTime.now());
        Pagamento salvo = pagamentoRepository.save(pagamento);
        System.out.println("[DEBUG-PROCESS] Pagamento salvo! Status agora é: " + salvo.getStatus());

        // marcar produto como vendido (usa ProdutoService)
        System.out.println("[DEBUG-PROCESS] Marcando produto ID " + pagamento.getProduto().getId() + " como VENDIDO...");
        produtoService.marcarComoVendido(pagamento.getProduto().getId());
        System.out.println("[DEBUG-PROCESS] Produto marcado como VENDIDO com sucesso!");
    }

    @Override
    @Transactional
    public void verificarEAtualizarSessao(String stripeSessionId) throws Exception {
        if (stripeSessionId == null || stripeSessionId.isBlank()) {
            throw new IllegalArgumentException("stripeSessionId é obrigatório");
        }

        if (stripeApiKey == null || stripeApiKey.isBlank()) {
            throw new IllegalStateException("Chave da Stripe não configurada");
        }

        Stripe.apiKey = stripeApiKey;

        try {
            System.out.println("[DEBUG] Verificando sessão Stripe: " + stripeSessionId);
            Session session = Session.retrieve(stripeSessionId);
            
            String paymentStatus = session.getPaymentStatus();
            System.out.println("[DEBUG] Payment Status (getPaymentStatus): " + paymentStatus);
            System.out.println("[DEBUG] Todos os detalhes: " + session.toJson());

            // Se a sessão foi completada (paid ou complete), processa
            if ("paid".equals(paymentStatus) || "complete".equals(paymentStatus)) {
                System.out.println("[DEBUG] Sessão COMPLETADA (status=" + paymentStatus + ") - processando...");
                processarSessaoStripeCompletada(stripeSessionId);
            } else {
                System.out.println("[DEBUG] Sessão NÃO completada. Status: " + paymentStatus);
            }
        } catch (com.stripe.exception.InvalidRequestException e) {
            // Sessão não encontrada - ignora e continua
            if (e.getMessage().contains("No such checkout.session")) {
                System.out.println("[WARN] Sessão não encontrada na Stripe: " + stripeSessionId);
                return;
            }
            System.out.println("[ERROR] Erro InvalidRequest: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Erro ao verificar sessão no Stripe: " + e.getMessage(), e);
        } catch (com.stripe.exception.StripeException e) {
            System.out.println("[ERROR] Erro Stripe: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Erro ao verificar sessão no Stripe: " + e.getMessage(), e);
        }
    }

    @Override
    public java.util.List<Pagamento> listarPorCompradorId(Long compradorId) {
        if (compradorId == null) throw new IllegalArgumentException("compradorId é obrigatório");
        return pagamentoRepository.findByCompradorIdOrderByDataCriacaoDesc(compradorId);
    }

    @Override
    public java.util.List<Pagamento> listarPorEmailComprador(String emailComprador) {
        if (emailComprador == null || emailComprador.isBlank()) throw new IllegalArgumentException("emailComprador é obrigatório");
        // procurar usuario por email
        var usuarioOpt = usuarioService.findByEmail(emailComprador);
        if (usuarioOpt.isEmpty()) return java.util.List.of();
        return listarPorCompradorId(usuarioOpt.get().getId());
    }
}
