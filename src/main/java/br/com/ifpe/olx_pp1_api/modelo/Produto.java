package br.com.ifpe.olx_pp1_api.modelo;

import java.time.LocalDate;
import java.util.Map;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.olx_pp1_api.config.JsonbConverter;
import br.com.ifpe.olx_pp1_api.util.entity.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Produto")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto extends EntidadeAuditavel {

    @Column(nullable = false)
    private String nome;

    @Column
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondicaoProduto condicao;

    @Column(nullable = false)
    private Double preco;

    @Column(nullable = false)
    private LocalDate dataPublicacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProduto status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaProduto categoriaProduto;

    @Convert(converter = JsonbConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> caracteristicas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) 
    private Usuario vendedor;

    @Column
    private String imagem;
}