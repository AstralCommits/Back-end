package br.com.trama.service;

import br.com.trama.dto.CenarioRequest;
import br.com.trama.dto.CenarioResponse;
import br.com.trama.dto.SimulacaoCenarioRequest;
import br.com.trama.dto.SimulacaoCenarioResponse;
import br.com.trama.error.BadRequestException;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.model.Categoria;
import br.com.trama.model.CenarioConcorrencia;
import br.com.trama.model.Usuario;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.repository.CategoriaRepository;
import br.com.trama.repository.CenarioConcorrenciaRepository;
import br.com.trama.repository.LancamentoMensalRepository;
import br.com.trama.repository.UsuarioRepository;
import br.com.trama.security.UsuarioPrincipal;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CenarioService {
    private final CenarioConcorrenciaRepository cenarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final LancamentoMensalRepository lancamentoRepository;

    public CenarioService(CenarioConcorrenciaRepository cenarioRepository,
                          UsuarioRepository usuarioRepository,
                          CategoriaRepository categoriaRepository,
                          LancamentoMensalRepository lancamentoRepository) {
        this.cenarioRepository = cenarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    @Transactional
    public CenarioResponse criar(CenarioRequest request, UsuarioPrincipal principal) {
        validarPercentual(request.percentualImpacto());
        Usuario usuario = usuarioRepository.findById(principal.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        Categoria categoria = request.categoriaId() == null ? null
                : categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
        CenarioConcorrencia cenario = new CenarioConcorrencia();
        cenario.setNome(request.nome().trim());
        cenario.setDescricao(request.descricao() == null ? null : request.descricao().trim());
        cenario.setPercentualImpacto(request.percentualImpacto());
        cenario.setUsuario(usuario);
        cenario.setCategoria(categoria);
        return toResponse(cenarioRepository.save(cenario));
    }

    @Transactional(readOnly = true)
    public List<CenarioResponse> listar(UsuarioPrincipal principal) {
        List<CenarioConcorrencia> cenarios = principal.getPerfil().name().equals("ADMIN")
                ? cenarioRepository.findAll()
                : cenarioRepository.findAllByUsuario_IdUsuarioOrderByIdCenarioDesc(principal.getIdUsuario());
        return cenarios.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SimulacaoCenarioResponse simular(SimulacaoCenarioRequest request, UsuarioPrincipal principal) {
        validarPercentual(request.percentualImpacto());
        Long idConfeccao = principal.getPerfil().name().equals("EMPRESARIO")
                ? principal.getIdConfeccao() : null;
        BigDecimal faturamentoReal = lancamentoRepository.somarFaturamentoAtivo(
                request.categoriaId(), idConfeccao, StatusLancamento.ATIVO);
        if (faturamentoReal == null) faturamentoReal = BigDecimal.ZERO;
        BigDecimal fator = BigDecimal.ONE.subtract(request.percentualImpacto()
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        BigDecimal hipotetico = faturamentoReal.multiply(fator).setScale(2, RoundingMode.HALF_UP);
        BigDecimal diferenca = faturamentoReal.subtract(hipotetico).setScale(2, RoundingMode.HALF_UP);
        return new SimulacaoCenarioResponse(faturamentoReal.setScale(2, RoundingMode.HALF_UP),
                hipotetico, diferenca, request.percentualImpacto());
    }

    private void validarPercentual(BigDecimal percentual) {
        if (percentual == null || percentual.compareTo(BigDecimal.ZERO) < 0
                || percentual.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException("O percentual de impacto deve estar entre 0 e 100.");
        }
    }

    private CenarioResponse toResponse(CenarioConcorrencia cenario) {
        return new CenarioResponse(cenario.getIdCenario(), cenario.getNome(), cenario.getDescricao(),
                cenario.getCategoria() == null ? null : cenario.getCategoria().getIdCategoria(),
                cenario.getPercentualImpacto());
    }
}
