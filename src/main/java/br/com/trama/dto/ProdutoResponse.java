package br.com.trama.dto;
import io.swagger.v3.oas.annotations.media.Schema; import java.math.BigDecimal;
@Schema(description="Produto cadastrado no catálogo")
public record ProdutoResponse(@Schema(example="12") Long id,@Schema(example="Bolsa de tecido") String nome,@Schema(example="Bolsa artesanal em tecido de algodão") String descricao,@Schema(example="49.90") BigDecimal preco,@Schema(example="3") Long idArtesao) {}
