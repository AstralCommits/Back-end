package br.com.trama.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SimulacaoCenarioRequest(

        Long categoriaId,

        @NotNull(message = "Informe o percentual de impacto.")
        @DecimalMin(value = "0.0", message = "O percentual não pode ser negativo.")
        @DecimalMax(value = "100.0", message = "O percentual não pode ultrapassar 100%.")
        BigDecimal percentualImpacto
) {
}
