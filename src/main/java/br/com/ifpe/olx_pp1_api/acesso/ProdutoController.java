package br.com.ifpe.olx_pp1_api.acesso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.ifpe.olx_pp1_api.dto.ProdutoRequest;
import br.com.ifpe.olx_pp1_api.dto.ProdutoResponse;
import br.com.ifpe.olx_pp1_api.modelo.CategoriaProduto;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.service.ProdutoService;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // criar produto
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<Produto> criarProduto(@PathVariable Long usuarioId, @RequestBody ProdutoRequest request) {
        Produto produto = request.build();
        Produto produtoSalvo = produtoService.criarProduto(produto, usuarioId);
        return new ResponseEntity<>(produtoSalvo, HttpStatus.CREATED);
    }

    // editar produto
    @PutMapping("/{id}")
    public ResponseEntity<Void> editarProduto(
            @PathVariable Long id,
            @RequestBody ProdutoRequest request) {

        Produto produto = request.build();
        produtoService.editarProduto(id, produto);
        return ResponseEntity.ok().build();
    }

    // excluir produto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirProduto(@PathVariable Long id) {
        produtoService.excluirProduto(id);
        return ResponseEntity.ok().build();
    }

    // marcar como vendido
    @PutMapping("/{id}/vendido")
    public ResponseEntity<Void> marcarComoVendido(@PathVariable Long id) {
        produtoService.marcarComoVendido(id);
        return ResponseEntity.ok().build();
    }

    // visualizar detalhes de um produto
    @GetMapping("/{id}")
    public ResponseEntity<Produto> visualizarDetalhes(@PathVariable Long id) {
        Produto produto = produtoService.visualizarDetalhes(id);
        return ResponseEntity.ok(produto);
    }

    // pesquisar produtos
    @GetMapping("/pesquisar")
    public List<Produto> pesquisarProdutos(@RequestParam String termo) {
        return produtoService.pesquisarProdutos(termo);
    }

    // listar todos ativos
    @GetMapping
    public List<Produto> listarTodosAtivos() {
        return produtoService.listarTodosAtivos();
    }

    // listar todos vendidos
    @GetMapping("/vendidos")
    public List<Produto> listarTodosVendidos() {
        return produtoService.listarTodosVendidos();
    }

    // listar por categoria
    @GetMapping("/categoria/{categoria}")
    public List<Produto> listarPorCategoria(@PathVariable CategoriaProduto categoria) {
        return produtoService.listarPorCategoria(categoria);
    }

    // listar produtos de um usuário específico
    @GetMapping("/usuario/{usuarioId}")
    public List<Produto> listarProdutosDeUsuario(@PathVariable Long usuarioId) {
        return produtoService.listarProdutosDeUsuario(usuarioId);
    }

    // listar produtos vendidos de um ususário específico
    @GetMapping("/usuario/{usuarioId}/vendidos")
    public List<Produto> listarProdutosVendidosDeUsuario(@PathVariable Long usuarioId) {
        return produtoService.listarProdutosVendidosDeUsuario(usuarioId);
    }

    @PostMapping("/{id}/imagem")
    public ResponseEntity<ProdutoResponse> uploadImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem) {

        Produto produto = produtoService.salvarImagem(id, imagem);
        return ResponseEntity.ok(ProdutoResponse.fromProduto(produto));
    }

    // pesquisa com múltiplos filtros
    @GetMapping("/pesquisar-avancado")
    public List<Produto> pesquisarProdutosComFiltros(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) CategoriaProduto categoria,
            @RequestParam(required = false) Double precoMin,
            @RequestParam(required = false) Double precoMax,
            @RequestParam(required = false) String uf) {

        return produtoService.pesquisarProdutosComFiltros(termo, categoria, precoMin, precoMax, uf);
    }
}
