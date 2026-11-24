package br.com.trama.dto;

import java.time.LocalDateTime;

public record AvaliacaoResponse(
        Long idAvaliacao,
        Long pedidoId,
        Long confeccaoId,
        Integer nota,
        String comentario,
        LocalDateTime dataAvaliacao) {
}
