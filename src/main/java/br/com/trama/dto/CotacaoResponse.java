package br.com.trama.dto;
import io.swagger.v3.oas.annotations.media.Schema; import br.com.trama.model.CotacaoCorporativa; import java.time.LocalDate; import java.util.List;
@Schema(description="Cotação corporativa criada")
public record CotacaoResponse(@Schema(example="25") Long id,@Schema(example="2026-12-15") LocalDate prazoEntrega,@Schema(example="EM_ANALISE_ARTESAO") String status,@Schema(example="[12, 13]") List<Long> produtos){ public static CotacaoResponse from(CotacaoCorporativa c){return new CotacaoResponse(c.getId(),c.getPrazoEntrega(),c.getStatus().name(),c.getItens().stream().map(i->i.getProduto().getId()).toList());}}
