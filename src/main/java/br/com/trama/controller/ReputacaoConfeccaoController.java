package br.com.trama.controller;

import br.com.trama.dto.ReputacaoConfeccaoResponse;
import br.com.trama.error.ResourceNotFoundException;
import br.com.trama.repository.ReputacaoConfeccaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/confeccoes")
@Tag(name = "Avaliações")
public class ReputacaoConfeccaoController {
    private final ReputacaoConfeccaoRepository repository;

    public ReputacaoConfeccaoController(ReputacaoConfeccaoRepository repository) { this.repository = repository; }

    @GetMapping("/{id}/reputacao")
    @Operation(summary = "Reputação da confecção",
            description = "Nota média e total de avaliações, lidos da view vw_reputacao_artesao. O id é o da confecção que recebeu a avaliação no pedido.")
    @ApiResponse(responseCode = "200", description = "Reputação da confecção")
    @ApiResponse(responseCode = "404", description = "Confecção não encontrada")
    public ReputacaoConfeccaoResponse reputacao(@PathVariable Long id) {
        return repository.buscarPorConfeccao(id)
                .orElseThrow(() -> new ResourceNotFoundException("Confecção não encontrada."));
    }
}
