package br.com.ifpe.olx_pp1_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ifpe.olx_pp1_api.modelo.Favorito;
import br.com.ifpe.olx_pp1_api.modelo.Produto;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    
    // verifica se já existe
    boolean existsByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
    
    // busca o favorito específico
    Optional<Favorito> findByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
    
    // lista todos favoritos de um usuário
    List<Favorito> findByUsuarioId(Long usuarioId);
    
    // remove um favorito específico
    void deleteByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
    
    // busca apenas os produtos favoritos de um usuário
    @Query("SELECT f.produto FROM Favorito f WHERE f.usuario.id = :usuarioId")
    List<Produto> findProdutosFavoritosByUsuarioId(@Param("usuarioId") Long usuarioId);
}
