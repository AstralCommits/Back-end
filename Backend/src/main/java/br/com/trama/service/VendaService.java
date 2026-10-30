package br.com.trama.service;

import br.com.trama.dto.VendaRequest;
import br.com.trama.dto.VendaResponse;
import br.com.trama.error.BadRequestException;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.model.Categoria;
import br.com.trama.model.Confeccao;
import br.com.trama.model.LancamentoMensal;
import br.com.trama.model.enums.OrigemDado;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.repository.CategoriaRepository;
import br.com.trama.repository.ConfeccaoRepository;
import br.com.trama.repository.LancamentoMensalRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendaService {
    private final LancamentoMensalRepository vendaRepository;
    private final ConfeccaoRepository confeccaoRepository;
    private final CategoriaRepository categoriaRepository;

    public VendaService(LancamentoMensalRepository vendaRepository,
                        ConfeccaoRepository confeccaoRepository,
                        CategoriaRepository categoriaRepository) {
        this.vendaRepository = vendaRepository;
        this.confeccaoRepository = confeccaoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public VendaResponse criar(VendaRequest request) {
        LocalDate mes = parseMes(request.mesAno());
        Confeccao confeccao = buscarConfeccao(request.idConfeccao());
        Categoria categoria = buscarCategoria(request.idCategoria());
        validarFonte(request.origemDado(), request.nomeFonte());
        validarDuplicidade(confeccao.getIdConfeccao(), categoria.getIdCategoria(), mes, null);

        LancamentoMensal venda = new LancamentoMensal();
        aplicar(venda, request, mes, confeccao, categoria);
        venda.setStatus(StatusLancamento.ATIVO);
        return toResponse(vendaRepository.saveAndFlush(venda));
    }

    @Transactional(readOnly = true)
    public List<VendaResponse> listar(Long idCategoria, Long idConfeccao, String mesAno) {
        LocalDate mes = (mesAno == null || mesAno.isBlank()) ? null : parseMes(mesAno);
        return vendaRepository.buscarComFiltros(idCategoria, idConfeccao, mes, StatusLancamento.ATIVO)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public VendaResponse buscar(Long id) { return toResponse(getEntity(id)); }

    @Transactional
    public VendaResponse atualizar(Long id, VendaRequest request) {
        LancamentoMensal venda = getEntity(id);
        if (venda.getStatus() == StatusLancamento.CANCELADO) {
            throw new BadRequestException("Um lançamento cancelado não pode ser atualizado.");
        }
        LocalDate mes = parseMes(request.mesAno());
        Confeccao confeccao = buscarConfeccao(request.idConfeccao());
        Categoria categoria = buscarCategoria(request.idCategoria());
        validarFonte(request.origemDado(), request.nomeFonte());
        validarDuplicidade(confeccao.getIdConfeccao(), categoria.getIdCategoria(), mes, id);
        aplicar(venda, request, mes, confeccao, categoria);
        return toResponse(vendaRepository.saveAndFlush(venda));
    }

    @Transactional
    public void cancelar(Long id) {
        LancamentoMensal venda = getEntity(id);
        if (venda.getStatus() == StatusLancamento.CANCELADO) {
            throw new BadRequestException("Este lançamento já está cancelado.");
        }
        venda.setStatus(StatusLancamento.CANCELADO);
        vendaRepository.saveAndFlush(venda);
    }

    private void aplicar(LancamentoMensal venda, VendaRequest request, LocalDate mes,
                         Confeccao confeccao, Categoria categoria) {
        venda.setMesReferencia(mes);
        venda.setQuantidade(request.quantidade());
        venda.setFaturamento(request.faturamento());
        venda.setOrigem(request.origem());
        venda.setOrigemDado(request.origemDado());
        venda.setNomeFonte(request.nomeFonte().trim());
        venda.setConfeccao(confeccao);
        venda.setCategoria(categoria);
    }

    private void validarFonte(OrigemDado origemDado, String nomeFonte) {
        if (origemDado == OrigemDado.FONTE_SECUNDARIA && (nomeFonte == null || nomeFonte.isBlank())) {
            throw new BadRequestException("Informe o nome da fonte secundária.");
        }
    }

    private void validarDuplicidade(Long idConfeccao, Long idCategoria, LocalDate mes, Long idAtual) {
        boolean existe = (idAtual == null)
                ? vendaRepository.existsByConfeccao_IdConfeccaoAndCategoria_IdCategoriaAndMesReferencia(
                        idConfeccao, idCategoria, mes)
                : vendaRepository.existsByConfeccao_IdConfeccaoAndCategoria_IdCategoriaAndMesReferenciaAndIdLancamentoNot(
                        idConfeccao, idCategoria, mes, idAtual);
        if (existe) {
            throw new BadRequestException(
                    "Já existe um lançamento para esta confecção, categoria e mês de referência.");
        }
    }

    private LocalDate parseMes(String mesAno) {
        try {
            return YearMonth.parse(mesAno).atDay(1);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new BadRequestException("O mês deve usar o formato AAAA-MM, por exemplo 2026-03.");
        }
    }

    private Confeccao buscarConfeccao(Long id) {
        return confeccaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Confecção não encontrada."));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
    }

    private LancamentoMensal getEntity(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lançamento não encontrado."));
    }

    private VendaResponse toResponse(LancamentoMensal l) {
        return new VendaResponse(l.getIdLancamento(), YearMonth.from(l.getMesReferencia()).toString(),
                l.getQuantidade(), l.getFaturamento(), l.getOrigem(), l.getOrigemDado(), l.getNomeFonte(),
                l.getStatus(), l.getConfeccao().getIdConfeccao(), l.getConfeccao().getNome(),
                l.getCategoria().getIdCategoria(), l.getCategoria().getNome());
    }
}
