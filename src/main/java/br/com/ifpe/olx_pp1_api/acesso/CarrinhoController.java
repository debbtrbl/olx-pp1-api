package br.com.ifpe.olx_pp1_api.acesso;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.olx_pp1_api.modelo.CarrinhoItem;
import br.com.ifpe.olx_pp1_api.service.CarrinhoService;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    // adicionar produto
    @PostMapping("/adicionar")
    public ResponseEntity<CarrinhoItem> adicionarItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long produtoId,
            @RequestParam(defaultValue = "1") Integer quantidade) {
        
        String email = userDetails.getUsername();
        CarrinhoItem item = carrinhoService.adicionarItem(email, produtoId, quantidade);
        
        return ResponseEntity.ok(item); 
    }

    // remover produto especifico
    @DeleteMapping("/remover/{produtoId}")
    public ResponseEntity<Void> removerItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long produtoId) {
        
        String email = userDetails.getUsername();
        carrinhoService.removerItem(email, produtoId);
        
        return ResponseEntity.ok().build();
    }

    // listar todos os produtos adicionados
    @GetMapping
    public ResponseEntity<List<CarrinhoItem>> listarCarrinho(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        List<CarrinhoItem> itens = carrinhoService.listarCarrinho(email);
        
        return ResponseEntity.ok(itens);
    }

    // calcular subtotal
    @GetMapping("/subtotal")
    public ResponseEntity<BigDecimal> getSubtotal(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        BigDecimal subtotal = carrinhoService.calcularSubtotal(email);
        
        return ResponseEntity.ok(subtotal);
    }

    // contar quatidade de produtos
    @GetMapping("/contar")
    public ResponseEntity<Integer> contarItens(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Integer quantidade = carrinhoService.contarItens(email);
        
        return ResponseEntity.ok(quantidade);
    }

    // limpar o carrinho
    @DeleteMapping("/limpar")
    public ResponseEntity<Void> limparCarrinho(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        carrinhoService.limparCarrinho(email);
        
        return ResponseEntity.ok().build();
    }
}