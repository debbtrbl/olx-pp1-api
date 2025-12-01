package br.com.ifpe.olx_pp1_api.modelo;

import br.com.ifpe.olx_pp1_api.util.entity.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Endereco")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Endereco extends EntidadeAuditavel {

    @Column
    private String cep;

    @Column
    private String logradouro;

    @Column
    private String numero;

    @Column
    private String complemento;

    @Column
    private String bairro;

    @Column
    private String cidade;

    @Column(length = 2)
    private String uf;
}