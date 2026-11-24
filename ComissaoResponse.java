package br.com.trama.dto;

import java.math.BigDecimal;

/**
 * Rateio de um pedido aprovado. Sempre vale valorTotal = repasseConfeccao + taxaPlataforma.
 */
public record ComissaoResponse(
        BigDecimal valorTotal,
        BigDecimal repasseConfeccao,
        BigDecimal taxaPlataforma) {
}
