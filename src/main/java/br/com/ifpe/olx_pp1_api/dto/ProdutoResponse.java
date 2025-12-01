package br.com.ifpe.olx_pp1_api.dto;

import java.time.LocalDate;
import java.util.Map;

import br.com.ifpe.olx_pp1_api.modelo.CategoriaProduto;
import br.com.ifpe.olx_pp1_api.modelo.CondicaoProduto;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.StatusProduto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private CondicaoProduto condicao;
    private Double preco;
    private LocalDate dataPublicacao;
    private StatusProduto status;
    private CategoriaProduto categoriaProduto;
    private String imagem;
    private Map<String, Object> caracteristicas;
    private String nomeVendedor; 
    private String telefoneVendedor;

    public static ProdutoResponse fromProduto(Produto produto) {
        return ProdutoResponse.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .condicao(produto.getCondicao())
                .preco(produto.getPreco())
                .dataPublicacao(produto.getDataPublicacao())
                .status(produto.getStatus())
                .categoriaProduto(produto.getCategoriaProduto())
                .caracteristicas(produto.getCaracteristicas())
                .nomeVendedor(produto.getVendedor().getNome()) 
                .telefoneVendedor(produto.getVendedor().getTelefone())
                .imagem(produto.getImagem())
                .build();
    }
}