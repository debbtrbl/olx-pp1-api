package br.com.ifpe.olx_pp1_api.dto;

import java.util.Set;
import java.time.LocalDate;
import br.com.ifpe.olx_pp1_api.modelo.Role;
import br.com.ifpe.olx_pp1_api.modelo.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import br.com.ifpe.olx_pp1_api.modelo.Endereco;

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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String uf;
    private String complemento;

    private Set<Role> roles;
    private boolean possuiCredenciaisMercadoPago;

    public static UsuarioResponse fromUsuario(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpfCnpj(usuario.getCpfCnpj())
                .telefone(usuario.getTelefone())
                .dataNascimento(usuario.getDataNascimento())

                .cep(endereco != null ? endereco.getCep() : null)
                .logradouro(endereco != null ? endereco.getLogradouro() : null)
                .numero(endereco != null ? endereco.getNumero() : null)
                .bairro(endereco != null ? endereco.getBairro() : null)
                .cidade(endereco != null ? endereco.getCidade() : null)
                .uf(endereco != null ? endereco.getUf() : null)
                .complemento(endereco != null ? endereco.getComplemento() : null)

                .roles(usuario.getRoles())
                .possuiCredenciaisMercadoPago(usuario.getMercadoPagoAccessToken() != null)
                .build();
    }
}