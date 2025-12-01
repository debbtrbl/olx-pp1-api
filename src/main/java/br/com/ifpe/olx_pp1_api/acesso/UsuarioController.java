package br.com.ifpe.olx_pp1_api.acesso;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.olx_pp1_api.dto.UsuarioResponse;
import br.com.ifpe.olx_pp1_api.dto.UsuarioUpdateRequest;
import br.com.ifpe.olx_pp1_api.modelo.Endereco;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import br.com.ifpe.olx_pp1_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername(); 
        
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no token"));

        return ResponseEntity.ok(UsuarioResponse.fromUsuario(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> updateMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {
        String email = userDetails.getUsername();
        
        Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no token"));

        if (request.getNome() != null) { 
            usuario.setNome(request.getNome()); 
        }
        if (request.getTelefone() != null) { 
            usuario.setTelefone(request.getTelefone()); 
        }
        
        if (request.getCep() != null) {
            
            if (usuario.getEndereco() == null) {
                usuario.setEndereco(new Endereco());
            }
    
            usuario.getEndereco().setCep(request.getCep());
        }
    

        Usuario usuarioSalvo = usuarioService.save(usuario);

        return ResponseEntity.ok(UsuarioResponse.fromUsuario(usuarioSalvo));
    }
}