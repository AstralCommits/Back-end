package br.com.trama.dto;

import br.com.trama.model.enums.PorteConfeccao;
import br.com.trama.model.enums.TipoConfeccao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ConfeccaoRequest(
        @NotBlank(message = "O nome da confecção é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @Pattern(regexp = "^(?:\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})?$",
                 message = "Informe um CNPJ com 14 dígitos, com ou sem pontuação.")
        String cnpj,

        @NotNull(message = "Informe o porte da confecção.")
        PorteConfeccao porte,

        @NotNull(message = "Informe o tipo de estabelecimento.")
        TipoConfeccao tipo,

        @NotBlank(message = "A cidade é obrigatória.")
        String cidade) {
}
