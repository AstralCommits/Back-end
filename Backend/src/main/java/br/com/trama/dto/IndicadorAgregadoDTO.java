package br.com.trama.dto;

import java.math.BigDecimal;

/**
 * Indicadores consolidados de uma categoria no período selecionado,
 * comparados com o período imediatamente anterior de mesma duração.
 */
public record IndicadorAgregadoDTO(
        Long categoriaId,
        String categoria,
        Long quantidadeAtual,
        BigDecimal faturamentoAtual,
        Long quantidadeAnterior,
        BigDecimal faturamentoAnterior,
        BigDecimal variacaoPercentual,
        String tendencia
) {
}
