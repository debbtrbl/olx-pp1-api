package br.com.ifpe.olx_pp1_api.dto;

import java.util.Set;

import br.com.ifpe.olx_pp1_api.modelo.Role;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class UsuarioResponse {
    
    private Long id;
    private String nome;
    private String email;
    private String cpfCnpj;
    private String telefone;
    private String cep;
    private Set<Role> roles;
    private boolean possuiCredenciaisMercadoPago;

    public static UsuarioResponse fromUsuario(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpfCnpj(usuario.getCpfCnpj())
                .telefone(usuario.getTelefone())
                
            
                .cep(usuario.getEndereco() != null ? usuario.getEndereco().getCep() : null)
    
                
                .roles(usuario.getRoles())
                .possuiCredenciaisMercadoPago(usuario.getMercadoPagoAccessToken() != null)
                .build();
    }
}