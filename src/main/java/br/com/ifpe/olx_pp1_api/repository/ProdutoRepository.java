package br.com.ifpe.olx_pp1_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.olx_pp1_api.modelo.CategoriaProduto;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.StatusProduto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    List<Produto> findByStatus(StatusProduto status); // produtos por status (ativo, vendido, inativo)
    
    List<Produto> findByCategoriaProdutoAndStatus(CategoriaProduto categoria, StatusProduto status); // produtos por categoria e status
    
    List<Produto> findByVendedorIdAndStatus(Long vendedorId, StatusProduto status); // produtos de um vendedor com status específico (vendido, ativo ou inativo)
    
    @Query("SELECT p FROM Produto p WHERE p.status = 'ATIVO' AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Produto> pesquisarProdutosAtivos(@Param("termo") String termo); // pesquisa por nome ou descrição
    
    List<Produto> findByVendedorId(Long vendedorId); // todos os produtos de um vendedor, independente do status


    // pesquisa com múltiplos filtros
    @Query("SELECT p FROM Produto p WHERE p.status = 'ATIVO' AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
           "AND (:categoria IS NULL OR p.categoriaProduto = :categoria) " +
           "AND (:precoMin IS NULL OR p.preco >= :precoMin) " +
           "AND (:precoMax IS NULL OR p.preco <= :precoMax) " +
           "AND (:uf IS NULL OR p.vendedor.endereco.uf = :uf)")
    List<Produto> pesquisarProdutosComFiltros(
            @Param("termo") String termo,
            @Param("categoria") CategoriaProduto categoria,
            @Param("precoMin") Double precoMin,
            @Param("precoMax") Double precoMax,
            @Param("uf") String uf);

    

}