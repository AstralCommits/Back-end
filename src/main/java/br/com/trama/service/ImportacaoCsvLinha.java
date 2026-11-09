package br.com.trama.service;

import br.com.trama.model.enums.OrigemDado;
import java.math.BigDecimal;
import java.time.YearMonth;

record ImportacaoCsvLinha(
        YearMonth mesAno,
        int quantidade,
        BigDecimal faturamento,
        Long idConfeccao,
        Long idCategoria,
        OrigemDado origemDado,
        String nomeFonte
) {}
