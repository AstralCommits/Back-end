package br.com.trama.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.trama.dto.IndicadorAgregadoDTO;
import br.com.trama.dto.IndicadorResponseDTO;
import br.com.trama.error.BadRequestException;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.repository.LancamentoMensalRepository;

/**
 * Testes do BACK-05 sem banco de dados: o repositório é simulado (Mockito).
 * Período usado: atual 2026-04..2026-06, anterior 2026-01..2026-03.
 */
class IndicadorServiceTest {

    private final LancamentoMensalRepository repo = mock(LancamentoMensalRepository.class);
    private final IndicadorService service = new IndicadorService(repo);

    /** Simula o que o banco devolve para um período: linhas [idCategoria, nome, quantidade, faturamento]. */
    private void periodo(String inicio, String fim, Object[]... linhas) {
        when(repo.agregarIndicadores(any(), any(),
                eq(LocalDate.parse(inicio)), eq(LocalDate.parse(fim)), eq(StatusLancamento.ATIVO)))
                .thenReturn(List.of(linhas));
    }

    private static Object[] linha(long id, String nome, long quantidade, Object faturamento) {
        return new Object[] {id, nome, quantidade, faturamento};
    }

    private IndicadorResponseDTO consultar() {
        return service.buscarIndicadores("2026-04", "2026-06", null, null);
    }

    @Test
    void criterioDeAceite_10milPara11mil_eAltaDe10Porcento() {
        periodo("2026-01-01", "2026-03-01", linha(1, "Moda Feminina", 100, new BigDecimal("10000.00")));
        periodo("2026-04-01", "2026-06-01", linha(1, "Moda Feminina", 120, new BigDecimal("11000.00")));

        IndicadorAgregadoDTO i = consultar().indicadores().get(0);

        assertThat(i.tendencia()).isEqualTo("ALTA");
        assertThat(i.variacaoPercentual()).isEqualByComparingTo("10.0");
        assertThat(i.faturamentoAtual()).isEqualByComparingTo("11000.00");
        assertThat(i.faturamentoAnterior()).isEqualByComparingTo("10000.00");
    }

    @Test
    void queda_de8Porcento() {
        periodo("2026-01-01", "2026-03-01", linha(2, "Fitness", 50, new BigDecimal("5000.00")));
        periodo("2026-04-01", "2026-06-01", linha(2, "Fitness", 46, new BigDecimal("4600.00")));

        IndicadorAgregadoDTO i = consultar().indicadores().get(0);

        assertThat(i.tendencia()).isEqualTo("QUEDA");
        assertThat(i.variacaoPercentual()).isEqualByComparingTo("-8.0");
    }

    @Test
    void comparaComOPeriodoAnteriorDeMesmaDuracao() {
        periodo("2026-04-01", "2026-06-01", linha(1, "Moda Feminina", 1, new BigDecimal("1")));

        IndicadorResponseDTO r = consultar();

        assertThat(r.inicioComparacao()).isEqualTo("2026-01");
        assertThat(r.fimComparacao()).isEqualTo("2026-03");
    }

    @Test
    void limitesDe5Porcento_eVariacaoExataNaClassificacao() {
        assertThat(tendenciaPara("10500")).isEqualTo("ALTA");     // +5,0% exato
        assertThat(tendenciaPara("9500")).isEqualTo("QUEDA");     // -5,0% exato
        assertThat(tendenciaPara("10499")).isEqualTo("ESTAVEL");
        assertThat(tendenciaPara("9501")).isEqualTo("ESTAVEL");
        assertThat(tendenciaPara("10000")).isEqualTo("ESTAVEL");
        assertThat(tendenciaPara("10499.6")).isEqualTo("ESTAVEL"); // +4,996%: aparece 5.0, mas não é ALTA
    }

    private String tendenciaPara(String faturamentoAtual) {
        periodo("2026-01-01", "2026-03-01", linha(1, "X", 1, new BigDecimal("10000")));
        periodo("2026-04-01", "2026-06-01", linha(1, "X", 1, new BigDecimal(faturamentoAtual)));
        return consultar().indicadores().get(0).tendencia();
    }

    @Test
    void aceitaSomaDevolvidaComoDouble() {
        periodo("2026-01-01", "2026-03-01", linha(1, "X", 1, 10000.0));
        periodo("2026-04-01", "2026-06-01", linha(1, "X", 1, 11000.0));

        assertThat(consultar().indicadores().get(0).tendencia()).isEqualTo("ALTA");
    }

    @Test
    void semFaturamentoAnterior_naoTemBaseDeComparacao() {
        periodo("2026-04-01", "2026-06-01", linha(3, "Moda Infantil", 30, new BigDecimal("3000.00")));

        IndicadorAgregadoDTO i = consultar().indicadores().get(0);

        assertThat(i.tendencia()).isEqualTo("SEM_BASE");
        assertThat(i.variacaoPercentual()).isNull();
    }

    @Test
    void semRegistrosNoPeriodo_devolveMensagemEstruturada() {
        IndicadorResponseDTO r = service.buscarIndicadores("2030-01", "2030-06", null, null);

        assertThat(r.mensagem()).isEqualTo("Não há dados para o período selecionado");
        assertThat(r.indicadores()).isEmpty();
        assertThat(r.totalQuantidade()).isZero();
    }

    @Test
    void comDados_mensagemVemNula() {
        periodo("2026-04-01", "2026-06-01", linha(1, "Moda Feminina", 1, new BigDecimal("1")));

        assertThat(consultar().mensagem()).isNull();
    }

    @Test
    void parametrosInvalidos_viramBadRequest() {
        assertThatThrownBy(() -> service.buscarIndicadores("2026-06", "2026-01", null, null))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> service.buscarIndicadores("janeiro", "2026-01", null, null))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> service.buscarIndicadores(null, "2026-01", null, null))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> service.buscarIndicadores("2026-01", "2026-02", 0L, null))
                .isInstanceOf(BadRequestException.class);
    }
}
