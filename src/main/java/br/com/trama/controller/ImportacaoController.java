package br.com.trama.controller;

import br.com.trama.dto.ImportacaoResponse;
import br.com.trama.security.UsuarioPrincipal;
import br.com.trama.service.ImportacaoService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importacoes")
public class ImportacaoController {
    private final ImportacaoService service;

    public ImportacaoController(ImportacaoService service) { this.service = service; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportacaoResponse> importar(
            @RequestPart("arquivo") MultipartFile arquivo,
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        ImportacaoResponse response = service.importar(arquivo, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ImportacaoResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return service.listar(principal);
    }
}
