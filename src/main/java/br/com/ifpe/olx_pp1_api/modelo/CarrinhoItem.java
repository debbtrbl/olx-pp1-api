package br.com.ifpe.olx_pp1_api.modelo;

import java.math.BigDecimal;

import br.com.ifpe.olx_pp1_api.util.entity.EntidadeAuditavel;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CarrinhoItem",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"usuario_id", "produto_id"}
       ))
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarrinhoItem extends EntidadeAuditavel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto; 

    @Builder.Default
    private Integer quantidade = 1;

    private BigDecimal precoUnitario; // preco do produto no momento da adicao ao carrinho

    // calcular subtotal
    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}