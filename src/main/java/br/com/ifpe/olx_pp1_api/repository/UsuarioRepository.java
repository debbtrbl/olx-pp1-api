package br.com.ifpe.olx_pp1_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ifpe.olx_pp1_api.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpfCnpj(String cpfCnpj);
    boolean existsByEmail(String email);
    boolean existsByCpfCnpj(String cpfCnpj);
}