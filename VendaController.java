package br.com.trama.controller;

import br.com.trama.dto.VendaRequest;
import br.com.trama.dto.VendaResponse;
import br.com.trama.service.VendaService;
import br.com.trama.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    public VendaController(VendaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRESARIO')")
    public ResponseEntity<VendaResponse> criar(
            @Valid @RequestBody VendaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) {

        VendaResponse response = service.criar(request, principal);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.idLancamento())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<VendaResponse> listar(
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) Long confeccao,
            @RequestParam(required = false) String mesAno,
            @AuthenticationPrincipal UsuarioPrincipal principal) {

        return service.listar(categoria, confeccao, mesAno, principal);
    }

    @GetMapping("/{id}")
    public VendaResponse buscar(@PathVariable Long id,
                                @AuthenticationPrincipal UsuarioPrincipal principal) {
        return service.buscar(id, principal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRESARIO')")
    public VendaResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody VendaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) {

        return service.atualizar(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPRESARIO')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
                                         @AuthenticationPrincipal UsuarioPrincipal principal) {

        service.cancelar(id, principal);

        return ResponseEntity.noContent().build();
    }
}
