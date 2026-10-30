package br.com.trama.controller;

import br.com.trama.dto.ConfeccaoRequest;
import br.com.trama.dto.ConfeccaoResponse;
import br.com.trama.service.ConfeccaoService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/confeccoes")
public class ConfeccaoController {
    private final ConfeccaoService service;

    public ConfeccaoController(ConfeccaoService service) { this.service = service; }

    @GetMapping
    public List<ConfeccaoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public ConfeccaoResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<ConfeccaoResponse> criar(@Valid @RequestBody ConfeccaoRequest request) {
        ConfeccaoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.idConfeccao()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ConfeccaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ConfeccaoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
