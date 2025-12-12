package br.com.ifpe.olx_pp1_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.olx_pp1_api.modelo.CarrinhoItem;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.StatusProduto;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.repository.CarrinhoRepository;
import br.com.ifpe.olx_pp1_api.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioService usuarioService;

    // adiciona produto ao carrinho
    @Transactional
    public CarrinhoItem adicionarItem(String emailUsuario, Long produtoId, Integer quantidade) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (!produto.getStatus().equals(StatusProduto.ATIVO)) {
            throw new RuntimeException("Produto não está disponível para compra");
        }

        Optional<CarrinhoItem> itemExistente = carrinhoRepository
            .findByUsuarioIdAndProdutoId(usuario.getId(), produtoId);

        if (itemExistente.isPresent()) {
            // se ja tiver adicionadno aumenta quantidade
            CarrinhoItem item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
            return carrinhoRepository.save(item);
        } else {
            // se n ele adiciona novo
            CarrinhoItem novoItem = CarrinhoItem.builder()
                .usuario(usuario)
                .produto(produto)
                .quantidade(quantidade)
                .precoUnitario(BigDecimal.valueOf(produto.getPreco())) 
                .build();
            
            return carrinhoRepository.save(novoItem);
        }
    }

    // remove produto do carrinho
    @Transactional
    public void removerItem(String emailUsuario, Long produtoId) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        carrinhoRepository.deleteByUsuarioIdAndProdutoId(usuario.getId(), produtoId);
    }

    // lista todos os produtos do carrinho
    public List<CarrinhoItem> listarCarrinho(String emailUsuario) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return carrinhoRepository.findByUsuarioId(usuario.getId());
    }

    // calcila subtotal
    public BigDecimal calcularSubtotal(String emailUsuario) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        BigDecimal subtotal = carrinhoRepository.calcularSubtotalCarrinho(usuario.getId());
        return subtotal != null ? subtotal : BigDecimal.ZERO;
    }

    // conta quantidade de produtos
    public Integer contarItens(String emailUsuario) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return carrinhoRepository.countByUsuarioId(usuario.getId());
    }

    // limpa carrinho
    @Transactional
    public void limparCarrinho(String emailUsuario) {
        Usuario usuario = usuarioService.findByEmail(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        carrinhoRepository.deleteByUsuarioId(usuario.getId());
    }
}