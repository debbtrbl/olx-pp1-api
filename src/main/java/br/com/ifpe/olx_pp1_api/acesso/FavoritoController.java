package br.com.ifpe.olx_pp1_api.acesso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.service.FavoritoService;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    // adicionar aofavorito
    @PostMapping("/produto/{produtoId}")
    public ResponseEntity<Void> adicionar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long produtoId) {
        
        String email = userDetails.getUsername(); 
        favoritoService.adicionarFavorito(email, produtoId);
        return ResponseEntity.ok().build();
    }

    // remover favorito
    @DeleteMapping("/produto/{produtoId}")
    public ResponseEntity<Void> remover(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long produtoId) {
        
        String email = userDetails.getUsername();
        favoritoService.removerFavorito(email, produtoId);
        return ResponseEntity.ok().build();
    }

    // listar favoritos do usuario
    @GetMapping
    public ResponseEntity<List<Produto>> listar(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        List<Produto> favoritos = favoritoService.listarFavoritos(email);
        return ResponseEntity.ok(favoritos);
    }

    // verificar se é favorito
    @GetMapping("/produto/{produtoId}/verificar")
    public ResponseEntity<Boolean> verificar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long produtoId) {
        
        String email = userDetails.getUsername();
        boolean isFavorito = favoritoService.verificarFavorito(email, produtoId);
        return ResponseEntity.ok(isFavorito);
    }
}