package br.com.trama.dto;

import java.math.BigDecimal;

public record CenarioResponse(
        Long idCenario,
        String nome,
        String descricao,
        Long categoriaId,
        BigDecimal percentualImpacto
) {
}
