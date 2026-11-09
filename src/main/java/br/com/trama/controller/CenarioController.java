package br.com.trama.controller;

import br.com.trama.dto.CenarioRequest;
import br.com.trama.dto.CenarioResponse;
import br.com.trama.dto.SimulacaoCenarioRequest;
import br.com.trama.dto.SimulacaoCenarioResponse;
import br.com.trama.security.UsuarioPrincipal;
import br.com.trama.service.CenarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/cenarios")
public class CenarioController {
    private final CenarioService service;

    public CenarioController(CenarioService service) { this.service = service; }

    @GetMapping
    public List<CenarioResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return service.listar(principal);
    }

    @PostMapping
    public ResponseEntity<CenarioResponse> criar(@Valid @RequestBody CenarioRequest request,
                                                  @AuthenticationPrincipal UsuarioPrincipal principal) {
        CenarioResponse response = service.criar(request, principal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.idCenario()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/simular")
    public SimulacaoCenarioResponse simular(@Valid @RequestBody SimulacaoCenarioRequest request,
                                            @AuthenticationPrincipal UsuarioPrincipal principal) {
        return service.simular(request, principal);
    }
}
