package br.com.trama.service;

import br.com.trama.dto.IndicadorAgregadoDTO;
import br.com.trama.dto.IndicadorResponseDTO;
import br.com.trama.error.BadRequestException;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.repository.LancamentoMensalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Calcula totais por categoria e compara o período selecionado
 * com o período anterior de mesma quantidade de meses.
 */
@Service
public class IndicadorService {

    public static final String MENSAGEM_SEM_DADOS = "Não há dados para o período selecionado";

    private static final BigDecimal LIMITE_TENDENCIA = new BigDecimal("5.00");
    private final LancamentoMensalRepository lancamentoMensalRepository;

    public IndicadorService(LancamentoMensalRepository lancamentoMensalRepository) {
        this.lancamentoMensalRepository = lancamentoMensalRepository;
    }

    @Transactional(readOnly = true)
    public IndicadorResponseDTO buscarIndicadores(
            String inicioTexto,
            String fimTexto,
            Long categoriaId,
            String cidade) {

        YearMonth inicio = converterMes(inicioTexto, "inicio");
        YearMonth fim = converterMes(fimTexto, "fim");

        if (inicio.isAfter(fim)) {
            throw new BadRequestException("O mês inicial não pode ser posterior ao mês final.");
        }
        if (categoriaId != null && categoriaId <= 0) {
            throw new BadRequestException("categoriaId deve ser maior que zero.");
        }

        String cidadeFiltro = (cidade == null || cidade.isBlank()) ? null : cidade.trim();

        long quantidadeMeses = ChronoUnit.MONTHS.between(inicio, fim) + 1;
        YearMonth inicioAnterior = inicio.minusMonths(quantidadeMeses);
        YearMonth fimAnterior = inicio.minusMonths(1);

        List<Object[]> atuais = consultar(
                categoriaId, cidadeFiltro, inicio, fim);
        List<Object[]> anteriores = consultar(
                categoriaId, cidadeFiltro, inicioAnterior, fimAnterior);

        // RF05: sem registros no período selecionado, devolve a mensagem estruturada.
        if (atuais.isEmpty()) {
            return new IndicadorResponseDTO(
                    inicio.toString(), fim.toString(),
                    inicioAnterior.toString(), fimAnterior.toString(),
                    cidadeFiltro, categoriaId,
                    0L, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    List.of(), MENSAGEM_SEM_DADOS);
        }

        Map<Long, DadosAgregados> mapaAtual = converterResultados(atuais);
        Map<Long, DadosAgregados> mapaAnterior = converterResultados(anteriores);

        // União preserva categorias que só tiveram movimento em um dos períodos.
        TreeSet<Long> ids = new TreeSet<>();
        ids.addAll(mapaAtual.keySet());
        ids.addAll(mapaAnterior.keySet());

        List<IndicadorAgregadoDTO> indicadores = new ArrayList<>();
        long totalQuantidade = 0L;
        BigDecimal totalFaturamento = BigDecimal.ZERO;

        for (Long id : ids) {
            DadosAgregados atual = mapaAtual.getOrDefault(id, DadosAgregados.vazio());
            DadosAgregados anterior = mapaAnterior.getOrDefault(id, DadosAgregados.vazio());

            // Classifica pelo valor exato; só a exibição é arredondada (1 casa).
            BigDecimal variacaoExata = calcularVariacao(
                    anterior.faturamento(), atual.faturamento());
            String tendencia = classificarTendencia(variacaoExata);
            BigDecimal variacao = variacaoExata == null
                    ? null : variacaoExata.setScale(1, RoundingMode.HALF_UP);

            String nomeCategoria = atual.nomeCategoria() != null
                    ? atual.nomeCategoria() : anterior.nomeCategoria();

            indicadores.add(new IndicadorAgregadoDTO(
                    id,
                    nomeCategoria,
                    atual.quantidade(),
                    atual.faturamento(),
                    anterior.quantidade(),
                    anterior.faturamento(),
                    variacao,
                    tendencia
            ));

            totalQuantidade += atual.quantidade();
            totalFaturamento = totalFaturamento.add(atual.faturamento());
        }

        return new IndicadorResponseDTO(
                inicio.toString(),
                fim.toString(),
                inicioAnterior.toString(),
                fimAnterior.toString(),
                cidadeFiltro,
                categoriaId,
                totalQuantidade,
                totalFaturamento.setScale(2, RoundingMode.HALF_UP),
                indicadores,
                null
        );
    }

    private List<Object[]> consultar(
            Long categoriaId, String cidade, YearMonth inicio, YearMonth fim) {
        return lancamentoMensalRepository.agregarIndicadores(
                categoriaId,
                cidade,
                inicio.atDay(1),
                fim.atDay(1),
                StatusLancamento.ATIVO
        );
    }

    private Map<Long, DadosAgregados> converterResultados(List<Object[]> resultados) {
        Map<Long, DadosAgregados> mapa = new LinkedHashMap<>();
        for (Object[] linha : resultados) {
            Long id = ((Number) linha[0]).longValue();
            String nome = (String) linha[1];
            Long quantidade = linha[2] == null ? 0L : ((Number) linha[2]).longValue();
            // O banco/JPA pode devolver BigDecimal ou Double na soma; converte sem estourar.
            BigDecimal faturamento = linha[3] == null
                    ? BigDecimal.ZERO : new BigDecimal(linha[3].toString());

            mapa.put(id, new DadosAgregados(
                    nome, quantidade, faturamento.setScale(2, RoundingMode.HALF_UP)));
        }
        return mapa;
    }

    private BigDecimal calcularVariacao(BigDecimal anterior, BigDecimal atual) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return null; // Não existe base matemática para calcular percentual.
        }
        return atual.subtract(anterior)
                .multiply(new BigDecimal("100"))
                .divide(anterior, 6, RoundingMode.HALF_UP);
    }

    private String classificarTendencia(BigDecimal variacao) {
        if (variacao == null) {
            return "SEM_BASE";
        }
        if (variacao.compareTo(LIMITE_TENDENCIA) >= 0) {
            return "ALTA";
        }
        if (variacao.compareTo(LIMITE_TENDENCIA.negate()) <= 0) {
            return "QUEDA";
        }
        return "ESTAVEL";
    }

    private YearMonth converterMes(String valor, String campo) {
        try {
            return YearMonth.parse(valor);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new BadRequestException(
                    "Parâmetro '" + campo + "' inválido. Use o formato AAAA-MM.");
        }
    }

    private record DadosAgregados(
            String nomeCategoria, Long quantidade, BigDecimal faturamento) {
        private static DadosAgregados vazio() {
            return new DadosAgregados(null, 0L, BigDecimal.ZERO);
        }
    }
}
