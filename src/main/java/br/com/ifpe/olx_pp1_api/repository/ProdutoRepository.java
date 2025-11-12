package br.com.ifpe.olx_pp1_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.olx_pp1_api.modelo.CategoriaProduto;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.StatusProduto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    List<Produto> findByStatus(StatusProduto status);
    
    List<Produto> findByCategoriaProdutoAndStatus(CategoriaProduto categoria, StatusProduto status);
    
    List<Produto> findByVendedorIdAndStatus(Long vendedorId, StatusProduto status);
    
    @Query("SELECT p FROM Produto p WHERE p.status = 'ATIVO' AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Produto> pesquisarProdutosAtivos(@Param("termo") String termo);
    
    List<Produto> findByVendedorId(Long vendedorId);
}