package br.com.trama.controller;

import br.com.trama.dto.IndicadorResponseDTO;
import br.com.trama.service.IndicadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta indicadores agregados de vendas.
 *
 * Exemplo:
 * GET /api/indicadores?inicio=2026-01&fim=2026-03&cidade=Recife
 */
@RestController
@RequestMapping("/api/indicadores")
public class IndicadorController {

    private final IndicadorService indicadorService;

    public IndicadorController(IndicadorService indicadorService) {
        this.indicadorService = indicadorService;
    }

    @GetMapping
    public ResponseEntity<IndicadorResponseDTO> buscarIndicadores(
            @RequestParam String inicio,
            @RequestParam String fim,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String cidade) {

        return ResponseEntity.ok(
                indicadorService.buscarIndicadores(inicio, fim, categoriaId, cidade)
        );
    }
}
