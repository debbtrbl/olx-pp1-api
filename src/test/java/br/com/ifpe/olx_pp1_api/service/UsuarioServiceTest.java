package br.com.ifpe.olx_pp1_api.service;

import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService; 

    @Mock
    private PasswordEncoder passwordEncoder; 

    @InjectMocks
    private UsuarioService usuarioService; 

   
    @Test
    @DisplayName("Deve enviar email de boas-vindas ao criar um novo usuário")
    void deveEnviarEmailAoCriarUsuario() {
        // ARRANGE
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail("teste@email.com");
        novoUsuario.setNome("Teste TDD");
        novoUsuario.setSenha("123456");

       
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
    
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");

        // ACT
        Usuario usuarioSalvoNoRetorno = usuarioService.registrarUsuario(novoUsuario);

        // ASSERT
        verify(emailService, times(1)).enviarEmail(
            eq("teste@email.com"), 
            anyString(), 
            anyString()
        );
    }

    // --- TESTE 2: Geração de Token e Desabilitação no Registro ---
    @Test
    @DisplayName("Deve gerar token de verificação e criar usuário desabilitado")
    void deveGerarTokenAoCriarUsuario() {
        // ARRANGE
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail("token@email.com");
        novoUsuario.setNome("Teste Token");
        novoUsuario.setSenha("123456");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");

        // ACT
        Usuario usuarioSalvo = usuarioService.registrarUsuario(novoUsuario); 

        // ASSERT
        // 1. Deve nascer desabilitado
        assertThat(usuarioSalvo.isHabilitado()).isFalse();
        
        // 2. Deve ter token
        assertThat(usuarioSalvo.getCodigoVerificacao()).isNotNull();
        assertThat(usuarioSalvo.getCodigoVerificacao()).isNotEmpty();

        // 3. Email deve conter o token
        verify(emailService, times(1)).enviarEmail(
            eq("token@email.com"),
            contains("Ative sua conta"),
            contains(usuarioSalvo.getCodigoVerificacao())
        );
    }

    // --- TESTE 3: Ativação de Conta ---
    @Test
    @DisplayName("Deve ativar usuário e limpar token quando receber código válido")
    void deveAtivarUsuarioComCodigoValido() {
        // ARRANGE
        String codigoValido = "uuid-valido-123";
        Usuario usuarioPendente = new Usuario();
        usuarioPendente.setEmail("pendente@email.com");
        usuarioPendente.setHabilitado(false);
        usuarioPendente.setCodigoVerificacao(codigoValido);

        when(usuarioRepository.findByCodigoVerificacao(codigoValido))
            .thenReturn(Optional.of(usuarioPendente));

        // ACT
        usuarioService.ativarConta(codigoValido);

        // ASSERT
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioAtivado = captor.getValue();

        assertThat(usuarioAtivado.isHabilitado()).isTrue();
        assertThat(usuarioAtivado.getCodigoVerificacao()).isNull();
    }
    
    // --- TESTE 4: Solicitação de Redefinição de Senha ---
    @Test
    @DisplayName("Deve gerar token de redefinição e enviar email quando solicitar troca de senha")
    void deveGerarTokenRedefinicaoSenha() {
        // ARRANGE
        String email = "esqueci@email.com";
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // ACT
        usuarioService.solicitarRedefinicaoSenha(email);

        // ASSERT
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();

        assertThat(usuarioSalvo.getTokenRedefinicaoSenha()).isNotNull();
        assertThat(usuarioSalvo.getDataExpiracaoRedefinicao()).isNotNull();

        verify(emailService).enviarEmail(
            eq(email),
            contains("Redefinição"),
            contains(usuarioSalvo.getTokenRedefinicaoSenha())
        );
    }

    // --- TESTE 5: Execução da Redefinição de Senha
    @Test
    @DisplayName("Deve trocar a senha e limpar o token se estiver válido e não expirado")
    void deveRedefinirSenhaComTokenValido() {
        // ARRANGE
        String token = "token-valido-123";
        String novaSenha = "senhaNova123";
        String senhaCriptografada = "hash-da-senha-nova";

        Usuario usuario = new Usuario();
        usuario.setTokenRedefinicaoSenha(token);
        
        usuario.setDataExpiracaoRedefinicao(java.time.LocalDateTime.now().plusMinutes(30));

        when(usuarioRepository.findByTokenRedefinicaoSenha(token))
            .thenReturn(java.util.Optional.of(usuario));

        when(passwordEncoder.encode(novaSenha)).thenReturn(senhaCriptografada);

        // ACT
        usuarioService.redefinirSenha(token, novaSenha);

        // ASSERT
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();

        // Senha foi trocada e criptografada?
        assertThat(usuarioSalvo.getSenha()).isEqualTo(senhaCriptografada);
        
        // Token foi limpo?
        assertThat(usuarioSalvo.getTokenRedefinicaoSenha()).isNull();
    }
}