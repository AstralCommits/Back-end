package br.com.trama.dto;
import io.swagger.v3.oas.annotations.media.Schema; import jakarta.validation.constraints.*; import java.time.LocalDate; import java.util.List;
@Schema(description="Solicitação corporativa de cotação") public record CotacaoRequest(@NotEmpty @Size(min=1) @Schema(example="[1, 2]") List<@NotNull Long> produtos,@NotNull @FutureOrPresent @Schema(example="2026-12-15") LocalDate prazoEntrega){}
