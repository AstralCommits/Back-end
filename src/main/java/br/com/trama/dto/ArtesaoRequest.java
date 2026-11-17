package br.com.trama.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
@Schema(description="Dados cadastrais do artesão")
public record ArtesaoRequest(
 @NotBlank @Size(max=120) @Schema(example="Maria da Silva") String nome,
 @NotBlank @CPF @Schema(example="529.982.247-25") String cpf,
 @NotBlank @Pattern(regexp="^\\+?[0-9() .-]{10,20}$",message="Telefone inválido.") @Schema(example="(87) 99999-0000") String telefone,
 @NotBlank @Pattern(regexp="^\\+?[0-9() .-]{10,20}$",message="WhatsApp inválido.") @Schema(example="(87) 99999-0000") String whatsapp,
 @Email @Size(max=160) @Schema(example="maria@email.com") String email) {}
