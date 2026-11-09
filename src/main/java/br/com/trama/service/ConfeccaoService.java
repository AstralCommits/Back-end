package br.com.trama.service;

import br.com.trama.dto.ConfeccaoRequest;
import br.com.trama.dto.ConfeccaoResponse;
import br.com.trama.error.BadRequestException;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.model.Cidade;
import br.com.trama.model.Confeccao;
import br.com.trama.repository.CidadeRepository;
import br.com.trama.repository.ConfeccaoRepository;
import br.com.trama.security.UsuarioPrincipal;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfeccaoService {
    private static final Map<String, String> CIDADES_PERMITIDAS = Map.of(
            "toritama", "Toritama",
            "caruaru", "Caruaru",
            "santa cruz do capibaribe", "Santa Cruz do Capibaribe");

    private final ConfeccaoRepository confeccaoRepository;
    private final CidadeRepository cidadeRepository;

    public ConfeccaoService(ConfeccaoRepository confeccaoRepository, CidadeRepository cidadeRepository) {
        this.confeccaoRepository = confeccaoRepository;
        this.cidadeRepository = cidadeRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<ConfeccaoResponse> listar(UsuarioPrincipal principal) {
        if (isEmpresario(principal)) {
            return confeccaoRepository.findById(principal.getIdConfeccao())
                    .map(this::toResponse).stream().toList();
        }
        return confeccaoRepository.findAllByOrderByNomeAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ConfeccaoResponse buscar(Long id, UsuarioPrincipal principal) {
        validarAcesso(id, principal);
        return toResponse(getEntity(id));
    }

    @Transactional
    public ConfeccaoResponse criar(ConfeccaoRequest request) {
        Confeccao confeccao = new Confeccao();
        aplicar(confeccao, request);
        return toResponse(confeccaoRepository.save(confeccao));
    }

    @Transactional
    public ConfeccaoResponse atualizar(Long id, ConfeccaoRequest request) {
        Confeccao confeccao = getEntity(id);
        aplicar(confeccao, request);
        return toResponse(confeccaoRepository.save(confeccao));
    }

    @Transactional
    public void excluir(Long id) {
        Confeccao confeccao = getEntity(id);
        confeccaoRepository.delete(confeccao);
        confeccaoRepository.flush();
    }

    private void aplicar(Confeccao confeccao, ConfeccaoRequest request) {
        String nome = request.nome().trim().replaceAll("\\s+", " ");
        Cidade cidade = resolverCidadePermitida(request.cidade());
        confeccao.setNome(nome);
        confeccao.setCnpj(normalizarCnpj(request.cnpj()));
        confeccao.setPorte(request.porte());
        confeccao.setTipo(request.tipo());
        confeccao.setCidade(cidade);
    }

    private Cidade resolverCidadePermitida(String valor) {
        String normalizado = normalizar(valor);
        String nomeOficial = CIDADES_PERMITIDAS.get(normalizado);
        if (nomeOficial == null) {
            throw new BadRequestException(
                    "Cidade inválida. Escolha Toritama, Caruaru ou Santa Cruz do Capibaribe.");
        }
        return cidadeRepository.findByNomeIgnoreCaseAndUf(nomeOficial, "PE")
                .orElseThrow(() -> new BadRequestException(
                        "A cidade permitida ainda não está cadastrada. Verifique a inicialização do sistema."));
    }

    private String normalizar(String valor) {
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) return null;
        String somenteNumeros = cnpj.replaceAll("\\D", "");
        if (somenteNumeros.length() != 14) {
            throw new BadRequestException("O CNPJ deve conter 14 dígitos.");
        }
        return somenteNumeros;
    }

    private void validarAcesso(Long idConfeccao, UsuarioPrincipal principal) {
        if (isEmpresario(principal) && !principal.getIdConfeccao().equals(idConfeccao)) {
            throw new ResourceNotFoundException("Confecção não encontrada.");
        }
    }

    private boolean isEmpresario(UsuarioPrincipal principal) {
        return principal != null && principal.getPerfil().name().equals("EMPRESARIO");
    }

    private Confeccao getEntity(Long id) {
        return confeccaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Confecção não encontrada."));
    }

    private ConfeccaoResponse toResponse(Confeccao c) {
        return new ConfeccaoResponse(c.getIdConfeccao(), c.getNome(), c.getCnpj(), c.getPorte(), c.getTipo(),
                c.getCidade().getIdCidade(), c.getCidade().getNome(), c.getCidade().getUf());
    }
}
