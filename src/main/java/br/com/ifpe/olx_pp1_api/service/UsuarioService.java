package br.com.ifpe.olx_pp1_api.service;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    public boolean existsByCpfCnpj(String cpfCnpj) {
        return usuarioRepository.existsByCpfCnpj(cpfCnpj);
    }
}