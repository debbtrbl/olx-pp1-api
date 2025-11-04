package br.com.ifpe.olx_pp1_api.modelo;

import java.time.LocalDate;
import java.util.Set;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.olx_pp1_api.util.entity.EntidadeAuditavel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Usuario")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario extends EntidadeAuditavel {

    @Column(nullable = false, length = 100) private String nome;
    @Column(unique = true, nullable = false, length = 14) private String cpfCnpj; 
    @Column private LocalDate dataNascimento;
    @Column(length = 15) private String telefone;
    @Column(length = 9) private String cep;
    @Column(nullable = false, unique = true, length = 100) private String email;
    @Column(nullable = false) private String senha; 

    @ElementCollection(fetch = FetchType.EAGER) 
    @CollectionTable(name = "UsuarioRole", joinColumns = @JoinColumn(name = "usuarioId"))
    @Enumerated(EnumType.STRING) 
    @Column(name = "role", nullable = false)
    private Set<Role> roles;
    
    @Column(name = "mp_access_token") private String mercadoPagoAccessToken;
    @Column(name = "mp_refresh_token") private String mercadoPagoRefreshToken;
}