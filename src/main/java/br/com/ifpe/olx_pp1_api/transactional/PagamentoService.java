package br.com.ifpe.olx_pp1_api.transactional;

public interface PagamentoService {
    Pagamento iniciarPagamento(Long produtoId, Long compradorId, Long unitAmountCents, Long quantity, String successUrl, String cancelUrl) throws Exception;
    java.util.List<Pagamento> iniciarPagamentoDoCarrinho(Long compradorId, String successUrl, String cancelUrl) throws Exception;
    void processarSessaoStripeCompletada(String stripeSessionId) throws Exception;
    void verificarEAtualizarSessao(String stripeSessionId) throws Exception;
    java.util.List<Pagamento> listarPorCompradorId(Long compradorId);
    java.util.List<Pagamento> listarPorEmailComprador(String emailComprador);
    String getStripeApiKey();
}
