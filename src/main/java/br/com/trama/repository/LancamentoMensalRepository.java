package br.com.trama.repository;

import br.com.trama.model.LancamentoMensal;
import br.com.trama.model.enums.StatusLancamento;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LancamentoMensalRepository extends JpaRepository<LancamentoMensal, Long> {
    boolean existsByConfeccao_IdConfeccaoAndCategoria_IdCategoriaAndMesReferencia(
            Long idConfeccao, Long idCategoria, LocalDate mesReferencia);

    boolean existsByConfeccao_IdConfeccaoAndCategoria_IdCategoriaAndMesReferenciaAndIdLancamentoNot(
            Long idConfeccao, Long idCategoria, LocalDate mesReferencia, Long idLancamento);

    @Query("""
            SELECT l FROM LancamentoMensal l
            WHERE l.status = :status
              AND (:idCategoria IS NULL OR l.categoria.idCategoria = :idCategoria)
              AND (:idConfeccao IS NULL OR l.confeccao.idConfeccao = :idConfeccao)
              AND (:mesReferencia IS NULL OR l.mesReferencia = :mesReferencia)
            ORDER BY l.mesReferencia DESC, l.idLancamento DESC
            """)
    List<LancamentoMensal> buscarComFiltros(
            @Param("idCategoria") Long idCategoria,
            @Param("idConfeccao") Long idConfeccao,
            @Param("mesReferencia") LocalDate mesReferencia,
            @Param("status") StatusLancamento status);
    @Query("""
            SELECT COALESCE(SUM(l.faturamento), 0) FROM LancamentoMensal l
            WHERE l.status = :status
              AND (:idCategoria IS NULL OR l.categoria.idCategoria = :idCategoria)
              AND (:idConfeccao IS NULL OR l.confeccao.idConfeccao = :idConfeccao)
            """)
    BigDecimal somarFaturamentoAtivo(
            @Param("idCategoria") Long idCategoria,
            @Param("idConfeccao") Long idConfeccao,
            @Param("status") StatusLancamento status);

    @Query("""
            SELECT l FROM LancamentoMensal l
            WHERE l.status = :status
              AND l.mesReferencia BETWEEN :inicio AND :fim
              AND (:idCategoria IS NULL OR l.categoria.idCategoria = :idCategoria)
              AND (:cidade IS NULL OR LOWER(l.confeccao.cidade.nome) = LOWER(:cidade))
              AND (:idConfeccao IS NULL OR l.confeccao.idConfeccao = :idConfeccao)
            ORDER BY l.mesReferencia
            """)
    List<LancamentoMensal> buscarParaIndicadores(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("idCategoria") Long idCategoria,
            @Param("cidade") String cidade,
            @Param("idConfeccao") Long idConfeccao,
            @Param("status") StatusLancamento status);

}

