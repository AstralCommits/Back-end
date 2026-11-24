package br.com.trama.dto;

import java.math.BigDecimal;

public record SimulacaoCenarioResponse(

        BigDecimal faturamentoReal,
        BigDecimal faturamentoHipotetico,
        BigDecimal diferencaReais,
        BigDecimal diferencaPercentual
) {
}
