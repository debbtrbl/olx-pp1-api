package br.com.ifpe.olx_pp1_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.olx_pp1_api.modelo.Favorito;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.repository.FavoritoRepository;
import br.com.ifpe.olx_pp1_api.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioService usuarioService;

    // adicionar aos favoritos
    @Transactional
    public void adicionarFavorito(String emailUsuario, Long produtoId) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        if (favoritoRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId)) {
            throw new RuntimeException("Produto já está nos favoritos!");
        }

        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

        Favorito favorito = Favorito.builder()
            .usuario(usuario)
            .produto(produto)
            .build();
        
        favoritoRepository.save(favorito);
    }

    // remover dos favoritos
    @Transactional
    public void removerFavorito(String emailUsuario, Long produtoId) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        favoritoRepository.deleteByUsuarioIdAndProdutoId(usuario.getId(), produtoId);
    }

    // listar favoritos do usuario
    public List<Produto> listarFavoritos(String emailUsuario) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return favoritoRepository.findProdutosFavoritosByUsuarioId(usuario.getId());
    }

    // verificar se ja eh favorito
    public boolean verificarFavorito(String emailUsuario, Long produtoId) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return favoritoRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId);
    }
}