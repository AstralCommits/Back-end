package br.com.trama.dto;

import br.com.trama.model.enums.OrigemDado;
import br.com.trama.model.enums.OrigemLancamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record VendaRequest(
        @NotNull(message = "Selecione a confecção.")
        Long idConfeccao,

        @NotNull(message = "Selecione a categoria.")
        Long idCategoria,

        @NotBlank(message = "Informe o mês de referência.")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$",
                 message = "O mês deve usar o formato AAAA-MM, por exemplo 2026-03.")
        String mesAno,

        @NotNull(message = "Informe a quantidade.")
        @PositiveOrZero(message = "A quantidade não pode ser negativa.")
        Integer quantidade,

        @NotNull(message = "Informe o faturamento.")
        @DecimalMin(value = "0.00", inclusive = true, message = "O faturamento não pode ser negativo.")
        @Digits(integer = 10, fraction = 2, message = "O faturamento deve ter até 10 dígitos inteiros e 2 casas decimais.")
        BigDecimal faturamento,

        @NotNull(message = "Informe como o registro foi incluído: MANUAL ou IMPORTACAO.")
        OrigemLancamento origem,

        @NotNull(message = "Informe a procedência do dado.")
        OrigemDado origemDado,

        @NotBlank(message = "Informe o nome da fonte ou da coleta.")
        @Size(max = 200, message = "O nome da fonte deve ter no máximo 200 caracteres.")
        String nomeFonte) {
}
