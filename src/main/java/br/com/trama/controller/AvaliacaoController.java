package br.com.trama.controller;

import br.com.trama.dto.AvaliacaoRequest;
import br.com.trama.dto.AvaliacaoResponse;
import br.com.trama.security.UsuarioPrincipal;
import br.com.trama.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/avaliacoes")
@Tag(name = "Avaliações")
public class AvaliacaoController {
    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Avalia um pedido entregue",
            description = "Recebe pedidoId, nota (1 a 5) e comentário. Só pedidos com status ENTREGUE podem ser avaliados, uma vez cada.")
    @ApiResponse(responseCode = "201", description = "Avaliação registrada")
    @ApiResponse(responseCode = "400", description = "Nota ou comentário inválidos")
    @ApiResponse(responseCode = "403", description = "Usuário não fez o pedido")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    @ApiResponse(responseCode = "409", description = "Pedido não entregue ou já avaliado")
    public ResponseEntity<AvaliacaoResponse> criar(@Valid @RequestBody AvaliacaoRequest request,
                                                    @AuthenticationPrincipal UsuarioPrincipal principal) {
        AvaliacaoResponse response = service.criar(request, principal);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.idAvaliacao()).toUri();
        return ResponseEntity.created(location).body(response);
    }
}
