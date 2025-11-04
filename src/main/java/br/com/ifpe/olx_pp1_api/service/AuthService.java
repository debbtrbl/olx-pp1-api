package br.com.ifpe.olx_pp1_api.service;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.ifpe.olx_pp1_api.config.JwtService;
import br.com.ifpe.olx_pp1_api.dto.AuthResponse;
import br.com.ifpe.olx_pp1_api.dto.LoginRequest;
import br.com.ifpe.olx_pp1_api.dto.RegisterRequest;
import br.com.ifpe.olx_pp1_api.modelo.Role;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request, Role role) {
        
        if (usuarioService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }
        if (usuarioService.existsByCpfCnpj(request.getCpfCnpj())) {
            throw new RuntimeException("Erro: CPF/CNPJ já está em uso!");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .cpfCnpj(request.getCpfCnpj())
                .telefone(request.getTelefone())
                .roles(Set.of(role))
                .build();
        
   
        usuario.setHabilitado(true); 


        Usuario usuarioSalvo = usuarioService.save(usuario);
        
        UserDetails userDetails = buildUserDetails(usuarioSalvo);
        String token = jwtService.generateToken(userDetails);
        
        return AuthResponse.builder()
                .token(token)
                .nomeUsuario(usuarioSalvo.getNome())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );
        var usuario = usuarioService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        UserDetails userDetails = buildUserDetails(usuario);
        String token = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .nomeUsuario(usuario.getNome())
                .build();
    }
    
    private UserDetails buildUserDetails(Usuario usuario) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getRoles().stream()
                        .map(r -> r.name().replace("ROLE_", ""))
                        .toArray(String[]::new))
                .build();
    }
}