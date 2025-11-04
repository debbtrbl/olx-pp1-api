package br.com.ifpe.olx_pp1_api.acesso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.olx_pp1_api.dto.AuthResponse;
import br.com.ifpe.olx_pp1_api.dto.LoginRequest;
import br.com.ifpe.olx_pp1_api.dto.RegisterRequest;
import br.com.ifpe.olx_pp1_api.modelo.Role;
import br.com.ifpe.olx_pp1_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
}