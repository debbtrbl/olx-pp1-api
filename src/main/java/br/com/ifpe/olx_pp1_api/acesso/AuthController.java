package br.com.ifpe.olx_pp1_api.acesso;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.olx_pp1_api.dto.AuthResponse;
import br.com.ifpe.olx_pp1_api.dto.EsqueciSenhaRequest;
import br.com.ifpe.olx_pp1_api.dto.LoginRequest;
import br.com.ifpe.olx_pp1_api.dto.RedefinirSenhaRequest;
import br.com.ifpe.olx_pp1_api.dto.RegisterRequest;
import br.com.ifpe.olx_pp1_api.modelo.Role;
import br.com.ifpe.olx_pp1_api.service.AuthService;
import br.com.ifpe.olx_pp1_api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;



    @PostMapping("/register/comprador")
    public ResponseEntity<AuthResponse> registerComprador(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request, Role.ROLE_COMPRADOR));
    }

    @PostMapping("/register/vendedor")
    public ResponseEntity<AuthResponse> registerVendedor(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request, Role.ROLE_VENDEDOR));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("codigo") String codigo) {
        usuarioService.ativarConta(codigo);
        return ResponseEntity.ok("Conta ativada com sucesso! Você já pode fazer login.");
    }



    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EsqueciSenhaRequest request) {
        usuarioService.solicitarRedefinicaoSenha(request.getEmail());
        return ResponseEntity.ok("Se o email existir, enviamos um link de redefinição.");
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody RedefinirSenhaRequest request) {
        usuarioService.redefinirSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok("Senha alterada com sucesso!");
    }
}