package br.com.ifpe.olx_pp1_api.dto;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UsuarioUpdateRequest {
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Size(min = 10, max = 15, message = "O telefone deve ser válido")
    private String telefone;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @Size(min = 8, max = 9, message = "O CEP deve ser válido")
    private String cep;

    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    
    @Size(min = 2, max = 2, message = "A UF deve ter 2 letras")
    private String uf;
    
    private String complemento;
}