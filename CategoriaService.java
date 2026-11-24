package br.com.trama.service;

import br.com.trama.dto.CategoriaRequest;
import br.com.trama.dto.CategoriaResponse;
import br.com.trama.error.BadRequestException;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.model.Categoria;
import br.com.trama.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repository.findAllByOrderByNomeAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscar(Long id) { return toResponse(getEntity(id)); }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        String nome = normalizarNome(request.nome());
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new BadRequestException("Categoria já cadastrada");
        }
        Categoria categoria = new Categoria(nome);
        return toResponse(repository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = getEntity(id);
        String nome = normalizarNome(request.nome());
        if (repository.existsByNomeIgnoreCaseAndIdCategoriaNot(nome, id)) {
            throw new BadRequestException("Categoria já cadastrada");
        }
        categoria.setNome(nome);
        return toResponse(repository.save(categoria));
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = getEntity(id);
        repository.delete(categoria);
        repository.flush();
    }

    private Categoria getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));
    }

    private String normalizarNome(String nome) { return nome.trim().replaceAll("\\s+", " "); }
    private CategoriaResponse toResponse(Categoria c) { return new CategoriaResponse(c.getIdCategoria(), c.getNome()); }
}
