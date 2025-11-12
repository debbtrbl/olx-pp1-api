package br.com.ifpe.olx_pp1_api.dto;

import java.util.Map;

import org.hibernate.validator.constraints.Length;

import br.com.ifpe.olx_pp1_api.modelo.CategoriaProduto;
import br.com.ifpe.olx_pp1_api.modelo.CondicaoProduto;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequest {

    @NotBlank(message = "O nome é de preenchimento obrigatório")
    @Length(max = 100, message = "O nome deverá ter no máximo {max} caracteres")
    private String nome;

    private String descricao;

    @NotNull(message = "A condição é de preenchimento obrigatório")
    private CondicaoProduto condicao;

    @NotNull(message = "O preço é de preenchimento obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private Double preco;

    @NotNull(message = "A categoria é de preenchimento obrigatório")
    private CategoriaProduto categoriaProduto;

   private Map<String, Object> caracteristicas;

    public Produto build() {
        return Produto.builder()
                .nome(nome)
                .descricao(descricao)
                .condicao(condicao)
                .preco(preco)
                .categoriaProduto(categoriaProduto)
                .caracteristicas(caracteristicas)
                .build();
    }
}