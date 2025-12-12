package br.com.ifpe.olx_pp1_api.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.olx_pp1_api.modelo.CarrinhoItem;
import jakarta.transaction.Transactional;

public interface CarrinhoRepository extends JpaRepository<CarrinhoItem, Long> {
    
    // verifica se ja ta no carrihno
    Optional<CarrinhoItem> findByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
    
    // lista produtos do carrinho de um usuario
    List<CarrinhoItem> findByUsuarioId(Long usuarioId);
    
    // remove produto especifico do carrinho
    @Transactional
    @Modifying
    void deleteByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
    
    // limpa carrinho 
    @Transactional
    @Modifying
    void deleteByUsuarioId(Long usuarioId);
    
    // conta produtos no carrinho
    Integer countByUsuarioId(Long usuarioId);
    
    // calcula subtotal
    @Query("SELECT SUM(ci.precoUnitario * ci.quantidade) FROM CarrinhoItem ci WHERE ci.usuario.id = :usuarioId")
    BigDecimal calcularSubtotalCarrinho(@Param("usuarioId") Long usuarioId);
}