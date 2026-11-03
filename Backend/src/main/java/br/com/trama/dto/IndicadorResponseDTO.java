package br.com.trama.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resposta do endpoint GET /api/indicadores.
 */
public record IndicadorResponseDTO(
        String inicio,
        String fim,
        String inicioComparacao,
        String fimComparacao,
        String cidade,
        Long categoriaId,
        Long totalQuantidade,
        BigDecimal totalFaturamento,
        List<IndicadorAgregadoDTO> indicadores
) {
}
