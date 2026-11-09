package br.com.trama.service;

import br.com.trama.dto.IndicadorResponseDTO;
import br.com.trama.error.BadRequestException;
import br.com.trama.model.LancamentoMensal;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.repository.LancamentoMensalRepository;
import br.com.trama.security.UsuarioPrincipal;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndicadorService {
    private final LancamentoMensalRepository repository;

    public IndicadorService(LancamentoMensalRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public IndicadorResponseDTO buscarIndicadores(String inicio, String fim, Long categoriaId, String cidade,
                                                   UsuarioPrincipal principal) {
        YearMonth mesInicio = parse(inicio, "início");
        YearMonth mesFim = parse(fim, "fim");
        if (mesFim.isBefore(mesInicio)) throw new BadRequestException("O mês final deve ser igual ou posterior ao mês inicial.");
        Long idConfeccao = principal != null && principal.getPerfil().name().equals("EMPRESARIO")
                ? principal.getIdConfeccao() : null;
        List<LancamentoMensal> lancamentos = repository.buscarParaIndicadores(
                mesInicio.atDay(1), mesFim.atEndOfMonth(), categoriaId,
                cidade == null || cidade.isBlank() ? null : cidade.trim(), idConfeccao,
                StatusLancamento.ATIVO);
        long quantidade = lancamentos.stream().mapToLong(LancamentoMensal::getQuantidade).sum();
        BigDecimal faturamento = lancamentos.stream().map(LancamentoMensal::getFaturamento)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal media = lancamentos.isEmpty() ? BigDecimal.ZERO.setScale(2)
                : faturamento.divide(BigDecimal.valueOf(lancamentos.size()), 2, RoundingMode.HALF_UP);
        return new IndicadorResponseDTO(mesInicio.toString(), mesFim.toString(), quantidade,
                faturamento, media, lancamentos.size(), categoriaId, cidade);
    }

    private YearMonth parse(String valor, String campo) {
        try { return YearMonth.parse(valor); }
        catch (DateTimeParseException | NullPointerException ex) {
            throw new BadRequestException("O mês de " + campo + " deve usar o formato AAAA-MM.");
        }
    }
}
