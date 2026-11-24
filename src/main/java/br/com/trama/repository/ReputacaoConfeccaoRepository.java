package br.com.trama.repository;

import br.com.trama.dto.ReputacaoConfeccaoResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Consulta somente leitura da view vw_reputacao_artesao (média e total de avaliações por confecção). */
@Repository
public class ReputacaoConfeccaoRepository {
    private final JdbcTemplate jdbc;

    public ReputacaoConfeccaoRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Optional<ReputacaoConfeccaoResponse> buscarPorConfeccao(Long confeccaoId) {
        List<ReputacaoConfeccaoResponse> linhas = jdbc.query(
                "SELECT artesao_id, artesao, cidade, uf, nota_media, total_avaliacoes "
                        + "FROM vw_reputacao_artesao WHERE artesao_id = ?",
                (rs, i) -> new ReputacaoConfeccaoResponse(
                        rs.getLong("artesao_id"),
                        rs.getString("artesao"),
                        rs.getString("cidade"),
                        rs.getString("uf"),
                        rs.getBigDecimal("nota_media"),
                        rs.getLong("total_avaliacoes")),
                confeccaoId);
        return linhas.stream().findFirst();
    }
}
