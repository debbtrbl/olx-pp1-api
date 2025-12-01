package br.com.ifpe.olx_pp1_api.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setHabilitado(false);
        usuario.setCodigoVerificacao(UUID.randomUUID().toString());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        String link = "http://localhost:8080/api/auth/verify?codigo=" + usuarioSalvo.getCodigoVerificacao();
        
        emailService.enviarEmail(
            usuarioSalvo.getEmail(), 
            "Ative sua conta", 
            "Clique no link para ativar: " + link
        );

        return usuarioSalvo;
    }


    public void reenviarCodigoAtivacao(Usuario usuario) {
        usuario.setCodigoVerificacao(UUID.randomUUID().toString());
        usuarioRepository.save(usuario);
        
       
        enviarEmailAtivacao(usuario);
    }

    
    private void enviarEmailAtivacao(Usuario usuario) {
       
        String link = "http://localhost:8080/api/auth/verify?codigo=" + usuario.getCodigoVerificacao();
        
        emailService.enviarEmail(
            usuario.getEmail(), 
            "Ative sua conta (Novo Código)", 
            "Parece que você tentou logar sem ativar. Aqui está um novo link: " + link
        );
    }
    
    public void ativarConta(String codigo) {
        Usuario usuario = usuarioRepository.findByCodigoVerificacao(codigo)
            .orElseThrow(() -> new RuntimeException("Código de verificação inválido ou expirado"));

        usuario.setHabilitado(true);
        usuario.setCodigoVerificacao(null);
        
        usuarioRepository.save(usuario);
    }




    public void solicitarRedefinicaoSenha(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setTokenRedefinicaoSenha(UUID.randomUUID().toString());
            usuario.setDataExpiracaoRedefinicao(LocalDateTime.now().plusHours(1));
            
            usuarioRepository.save(usuario);
            
            emailService.enviarEmail(
                usuario.getEmail(), 
                "Redefinição de Senha", 
                "Seu token é: " + usuario.getTokenRedefinicaoSenha()
            );
        }
    }


    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository.findByTokenRedefinicaoSenha(token)
            .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (usuario.getDataExpiracaoRedefinicao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTokenRedefinicaoSenha(null);
        usuario.setDataExpiracaoRedefinicao(null);

        usuarioRepository.save(usuario);
    }


    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public boolean existsByCpfCnpj(String cpfCnpj) {
  
        return false; 
    }
}