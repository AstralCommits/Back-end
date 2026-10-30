package br.com.trama.controller;

import br.com.trama.dto.VendaRequest;
import br.com.trama.dto.VendaResponse;
import br.com.trama.service.VendaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {
    private final VendaService service;

    public VendaController(VendaService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<VendaResponse> criar(@Valid @RequestBody VendaRequest request) {
        VendaResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.idLancamento()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<VendaResponse> listar(
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) Long confeccao,
            @RequestParam(required = false) String mesAno) {
        return service.listar(categoria, confeccao, mesAno);
    }

    @GetMapping("/{id}")
    public VendaResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PutMapping("/{id}")
    public VendaResponse atualizar(@PathVariable Long id, @Valid @RequestBody VendaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
