package br.com.trama.controller;

import br.com.trama.dto.CidadeResponse;
import br.com.trama.repository.CidadeRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cidades")
public class CidadeController {
    private static final List<String> PERMITIDAS = List.of("Toritama", "Caruaru", "Santa Cruz do Capibaribe");
    private final CidadeRepository repository;

    public CidadeController(CidadeRepository repository) { this.repository = repository; }

    @GetMapping
    public List<CidadeResponse> listarPermitidas() {
        return PERMITIDAS.stream()
                .map(nome -> repository.findByNomeIgnoreCaseAndUf(nome, "PE")
                        .map(c -> new CidadeResponse(c.getIdCidade(), c.getNome(), c.getUf())).orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
