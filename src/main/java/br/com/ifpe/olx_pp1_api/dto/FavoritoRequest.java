package br.com.ifpe.olx_pp1_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoRequest {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private BigDecimal produtoPreco;
    private String produtoImagem;
    private LocalDateTime dataAdicionado;
}
