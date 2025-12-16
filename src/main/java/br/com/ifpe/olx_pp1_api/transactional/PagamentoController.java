package br.com.ifpe.olx_pp1_api.transactional;

import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.service.ProdutoService;
import br.com.ifpe.olx_pp1_api.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagamento")
@CrossOrigin(origins = "*")
public class PagamentoController {

    private static final Logger log = LoggerFactory.getLogger(PagamentoController.class);

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping(
            value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createCheckoutSession(
            @RequestBody CreateCheckoutRequest req,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Usuário autenticado
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Usuário não autenticado"
                ));
            }

            // Validação básica
            if (req.getProdutoId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "produtoId é obrigatório"
                ));
            }

            // Resolve comprador pelo e-mail do token
            Usuario comprador = usuarioService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            // Busca produto
            Produto produto = produtoService.visualizarDetalhes(req.getProdutoId());
            if (produto == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Produto não encontrado"
                ));
            }

            // Calcula valores
            Long precoEmCentavos = (long) (produto.getPreco() * 100);
            Long quantidade = (req.getQuantity() != null && req.getQuantity() > 0)
                    ? req.getQuantity()
                    : 1L;

            log.info(
                    "Iniciando pagamento: produto={}, comprador={}, valor={}, quantidade={}",
                    produto.getId(),
                    comprador.getId(),
                    precoEmCentavos,
                    quantidade
            );

            // Delegação TOTAL para o service
            Pagamento pagamento = pagamentoService.iniciarPagamento(
                    produto.getId(),
                    comprador.getId(),
                    precoEmCentavos,
                    quantidade,
                    req.getSuccessUrl(),
                    req.getCancelUrl()
            );

            // Resposta mínima e segura
            return ResponseEntity.ok(Map.of(
                    "pagamentoId", pagamento.getId(),
                    "status", pagamento.getStatus(),
                    "checkoutUrl", pagamento.getCheckoutUrl()
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Erro ao iniciar pagamento", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Erro interno ao iniciar pagamento"
            ));
        }
    }

        @PostMapping(
            value = "/create-from-cart",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
        )
        public ResponseEntity<?> createCheckoutFromCart(
            @RequestBody CreateCheckoutRequest req,
            @AuthenticationPrincipal UserDetails userDetails
        ) {
        try {
            // Usuário autenticado
            if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Usuário não autenticado"
            ));
            }

            // Resolve comprador pelo e-mail do token
            Usuario comprador = usuarioService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            // Delegação para o service: cria um pagamento POR item e uma sessão única
            var pagamentos = pagamentoService.iniciarPagamentoDoCarrinho(
                comprador.getId(), req.getSuccessUrl(), req.getCancelUrl()
            );

            // todos compartilham a mesma checkoutUrl e sessionId, então pega do primeiro
            String checkoutUrl = pagamentos.isEmpty() ? null : pagamentos.get(0).getCheckoutUrl();
            var ids = pagamentos.stream().map(Pagamento::getId).toList();

            return ResponseEntity.ok(Map.of(
                "pagamentoIds", ids,
                "checkoutUrl", checkoutUrl
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Erro ao iniciar pagamento (carrinho)", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erro interno ao iniciar pagamento"
            ));
        }
        }

    @GetMapping("/testar-stripe")
    public ResponseEntity<?> testarStripe() {
        try {
            if (pagamentoService.getStripeApiKey() == null || pagamentoService.getStripeApiKey().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "Chave da Stripe não está configurada"
                ));
            }

            com.stripe.Stripe.apiKey = pagamentoService.getStripeApiKey();
            
            // Tenta recuperar uma lista de customers (teste simples)
            var customers = com.stripe.model.Customer.list(new java.util.HashMap<>());
            
            return ResponseEntity.ok(Map.of(
                    "status", "OK",
                    "mensagem", "Conexão com Stripe funcionando",
                    "chaveConfigurада", pagamentoService.getStripeApiKey().substring(0, 10) + "...",
                    "customersCount", customers.getData().size()
            ));
        } catch (Exception e) {
            log.error("Erro ao testar Stripe", e);
            return ResponseEntity.status(500).body(Map.of(
                    "erro", "Falha na conexão com Stripe: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/verificar/{sessionId}")
    public ResponseEntity<?> verificarSessao(@PathVariable String sessionId) {
        try {
            if (sessionId == null || sessionId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "sessionId é obrigatório"
                ));
            }

            pagamentoService.verificarEAtualizarSessao(sessionId);

            return ResponseEntity.ok(Map.of(
                    "message", "Sessão verificada e atualizada com sucesso"
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao verificar sessão: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Erro ao verificar sessão", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Erro ao verificar sessão: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> listarPorUsuarioId(@PathVariable Long id) {
        var lista = pagamentoService.listarPorCompradorId(id);
        var resp = lista.stream()
                .map(br.com.ifpe.olx_pp1_api.dto.PagamentoResponse::fromPagamento)
                .toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> listarMe(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            String email = userDetails.getUsername();
            log.info("Listando pagamentos para usuário: {}", email);
            
            var lista = pagamentoService.listarPorEmailComprador(email);
            log.info("Encontrados {} pagamentos", lista.size());

            // Verifica os pagamentos PENDENTES na Stripe e atualiza automaticamente
            for (Pagamento pagamento : lista) {
                if (pagamento.getStatus() == StatusPagamento.PENDENTE && 
                    pagamento.getStripeSessionId() != null) {
                    try {
                        log.info("Verificando pagamento ID {} com sessionId: {}", 
                                pagamento.getId(), pagamento.getStripeSessionId());
                        pagamentoService.verificarEAtualizarSessao(pagamento.getStripeSessionId());
                        log.info("Pagamento ID {} verificado com sucesso", pagamento.getId());
                    } catch (Exception e) {
                        log.warn("Erro ao verificar sessão {} na Stripe: {}", 
                                pagamento.getStripeSessionId(), e.getMessage(), e);
                        // continua mesmo se falhar para uma sessão
                    }
                }
            }

            // Recarrega a lista após as verificações
            lista = pagamentoService.listarPorEmailComprador(email);
            var resp = lista.stream()
                    .map(br.com.ifpe.olx_pp1_api.dto.PagamentoResponse::fromPagamento)
                    .toList();

            log.info("Retornando {} pagamentos", resp.size());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("Erro ao listar pagamentos do usuário", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Erro ao listar pagamentos"
            ));
        }
    }
}
