package br.com.ifpe.olx_pp1_api.dto;

import br.com.ifpe.olx_pp1_api.transactional.Pagamento;
import br.com.ifpe.olx_pp1_api.transactional.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoResponse {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private StatusPagamento status;
    private Long amountCents;
    private Long quantity;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConfirmacao;
    private String checkoutUrl;

    public static PagamentoResponse fromPagamento(Pagamento p) {
        return PagamentoResponse.builder()
                .id(p.getId())
                .produtoId(p.getProduto() != null ? p.getProduto().getId() : null)
                .produtoNome(p.getProduto() != null ? p.getProduto().getNome() : null)
                .status(p.getStatus())
                .amountCents(p.getAmountCents())
                .quantity(p.getQuantity())
                .dataCriacao(p.getDataCriacao())
                .dataConfirmacao(p.getDataConfirmacao())
                .checkoutUrl(p.getCheckoutUrl())
                .build();
    }
}
