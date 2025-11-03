package br.com.ifpe.olx_pp1_api.api.vendedor;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.ifpe.olx_pp1_api.modelo.vendedor.Vendedor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorRequest {

   private String nome;

   @JsonFormat(pattern = "dd/MM/yyyy")
   private LocalDate dataNascimento;

   private String cpf;

   private String telefone;

   private String cep;

   public Vendedor build(){
        return Vendedor.builder()
                .nome(nome)
                .dataNascimento(dataNascimento)
                .cpf(cpf)
                .telefone(telefone)
                .cep(cep)
                .build();
   }
}
