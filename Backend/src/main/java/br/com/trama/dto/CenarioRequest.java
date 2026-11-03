package br.com.trama.dto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CenarioRequest(

        @NotBlank(message = "Informe o nome do cenário.")
        String nome,

        String descricao,

        Long categoriaId,

        Long usuarioId,

        @NotNull(message = "Informe o percentual de impacto.")
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "100.0")
        BigDecimal percentualImpacto
) {
}
