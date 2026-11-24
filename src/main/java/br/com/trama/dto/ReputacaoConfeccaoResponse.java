package br.com.trama.dto;

import java.math.BigDecimal;

/** Reputação da confecção (linha da view vw_reputacao_artesao do banco). notaMedia é nula sem avaliações. */
public record ReputacaoConfeccaoResponse(
        Long confeccaoId,
        String confeccao,
        String cidade,
        String uf,
        BigDecimal notaMedia,
        Long totalAvaliacoes) {
}
