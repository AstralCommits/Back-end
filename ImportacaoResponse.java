package br.com.trama.dto;

import br.com.trama.model.enums.StatusImportacao;
import java.time.LocalDateTime;

public record ImportacaoResponse(
        Long idImportacao,
        String nomeArquivo,
        LocalDateTime dataImportacao,
        Integer qtdLinhas,
        StatusImportacao status,
        Long idUsuario
) {}
