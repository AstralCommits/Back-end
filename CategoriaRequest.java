package br.com.trama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "O nome da categoria é obrigatório.")
        @Size(max = 50, message = "O nome da categoria deve ter no máximo 50 caracteres.")
        String nome) {
}
