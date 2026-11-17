package br.com.trama.controller;
import br.com.trama.dto.ArtesaoRequest; import br.com.trama.model.Artesao; import br.com.trama.repository.ArtesaoRepository;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid;
import java.net.URI; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
@RestController @RequestMapping("/api/artesaos") @Tag(name="Artesãos",description="Cadastro e consulta de artesãos")
public class ArtesaoController { private final ArtesaoRepository repo; public ArtesaoController(ArtesaoRepository r){repo=r;}
 @PostMapping @Operation(summary="Cadastrar artesão",description="Valida nome, CPF, telefone e WhatsApp.")
 public ResponseEntity<Artesao> criar(@Valid @RequestBody ArtesaoRequest dto){ Artesao a=new Artesao(); a.setNome(dto.nome()); a.setCpf(dto.cpf()); a.setTelefone(dto.telefone()); a.setWhatsapp(dto.whatsapp()); a.setEmail(dto.email()); Artesao salvo=repo.save(a); URI uri=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(salvo.getId()).toUri(); return ResponseEntity.created(uri).body(salvo); }
 @GetMapping @Operation(summary="Listar artesãos") public java.util.List<Artesao> listar(){return repo.findAll();}
}
