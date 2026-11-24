package br.com.trama.dto;

import br.com.trama.model.enums.OrigemDado;
import br.com.trama.model.enums.OrigemLancamento;
import br.com.trama.model.enums.StatusLancamento;
import java.math.BigDecimal;

public record VendaResponse(
        Long idLancamento,
        String mesAno,
        Integer quantidade,
        BigDecimal faturamento,
        OrigemLancamento origem,
        OrigemDado origemDado,
        String nomeFonte,
        StatusLancamento status,
        Long idConfeccao,
        String confeccao,
        Long idCategoria,
        String categoria) {
}
