package br.com.ifpe.olx_pp1_api.dto;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UsuarioUpdateRequest {
    @Size(min = 3, max = 100) private String nome;
    @Size(min = 10, max = 15) private String telefone;
    @Size(min = 8, max = 9) private String cep;
}