package br.com.ifpe.olx_pp1_api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.ifpe.olx_pp1_api.modelo.CategoriaProduto;
import br.com.ifpe.olx_pp1_api.modelo.Produto;
import br.com.ifpe.olx_pp1_api.modelo.StatusProduto;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.repository.ProdutoRepository;
import br.com.ifpe.olx_pp1_api.repository.UsuarioRepository;
import br.com.ifpe.olx_pp1_api.util.Util;
import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // criar produto (passando o id do usuário vendedor)
    @Transactional
    public Produto criarProduto(Produto produto, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        produto.setVendedor(usuario);
        produto.setDataPublicacao(LocalDate.now());
        produto.setStatus(StatusProduto.ATIVO);
        produto.setHabilitado(Boolean.TRUE);

        return repository.save(produto);
    }

    // editar produto
    @Transactional
    public void editarProduto(Long id, Produto produtoAlterado) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(produtoAlterado.getNome());
        produto.setDescricao(produtoAlterado.getDescricao());
        produto.setCondicao(produtoAlterado.getCondicao());
        produto.setPreco(produtoAlterado.getPreco());
        produto.setCategoriaProduto(produtoAlterado.getCategoriaProduto());
        produto.setCaracteristicas(produtoAlterado.getCaracteristicas());

        repository.save(produto);
    }

    // excluir produto
    @Transactional
    public void excluirProduto(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setHabilitado(Boolean.FALSE);
        repository.save(produto);
    }

    // marcar como vendido
    @Transactional
    public void marcarComoVendido(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setStatus(StatusProduto.VENDIDO);
        repository.save(produto);
    }

    // listar produtos de um usuário específico
    public List<Produto> listarProdutosDeUsuario(Long usuarioId) {
        return repository.findByVendedorId(usuarioId);
    }

    // listar produtos vendidos de um ususário específico
    public List<Produto> listarProdutosVendidosDeUsuario(Long usuarioId) {
        return repository.findByVendedorIdAndStatus(usuarioId, StatusProduto.VENDIDO);
    }

    // visualizar detalhes
    public Produto visualizarDetalhes(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    // pesquisar produtos
    public List<Produto> pesquisarProdutos(String termo) {
        return repository.pesquisarProdutosAtivos(termo);
    }

    // listar todos ativos
    public List<Produto> listarTodosAtivos() {
        return repository.findByStatus(StatusProduto.ATIVO);
    }

    // listar todos os vendidos
    public List<Produto> listarTodosVendidos() {
        return repository.findByStatus(StatusProduto.VENDIDO);
    }

    // listar por categoria
    public List<Produto> listarPorCategoria(CategoriaProduto categoria) {
        return repository.findByCategoriaProdutoAndStatus(categoria, StatusProduto.ATIVO);
    }

    // obter por ID
    public Produto obterPorID(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    // salvar imagem do produto
    @Transactional
    public Produto salvarImagem(Long id, MultipartFile imagem) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        String imagemUpada = Util.fazerUploadImagem(imagem);

        if (imagemUpada != null) {
            produto.setImagem(imagemUpada);
        }

        return repository.save(produto);
    }

    // pesquisa com múltiplos filtros
    public List<Produto> pesquisarProdutosComFiltros(String termo, CategoriaProduto categoria,
            Double precoMin, Double precoMax, String uf) {
        return repository.pesquisarProdutosComFiltros(termo, categoria, precoMin, precoMax, uf);
    }

}
