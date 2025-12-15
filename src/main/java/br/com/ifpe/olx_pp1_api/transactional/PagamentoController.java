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

        String email = userDetails.getUsername();
        var lista = pagamentoService.listarPorEmailComprador(email);
        var resp = lista.stream()
                .map(br.com.ifpe.olx_pp1_api.dto.PagamentoResponse::fromPagamento)
                .toList();

        return ResponseEntity.ok(resp);
    }
}
