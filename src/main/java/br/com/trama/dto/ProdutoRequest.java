package br.com.trama.dto;
import io.swagger.v3.oas.annotations.media.Schema; import jakarta.validation.constraints.*; import java.math.BigDecimal;
@Schema(description="Produto artesanal oferecido por um artesão") public record ProdutoRequest(@NotBlank @Size(max=120) @Schema(example="Bolsa de tecido") String nome,@Size(max=500) String descricao,@NotNull @DecimalMin(value="0.01") @Schema(example="49.90") BigDecimal preco,@NotNull Long idArtesao){}
