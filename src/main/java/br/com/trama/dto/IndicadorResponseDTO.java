package br.com.trama.dto;

import java.math.BigDecimal;

public record IndicadorResponseDTO(
        String inicio,
        String fim,
        Long quantidadeTotal,
        BigDecimal faturamentoTotal,
        BigDecimal faturamentoMedio,
        long quantidadeLancamentos,
        Long categoriaId,
        String cidade
) {}
