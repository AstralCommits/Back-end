package br.com.trama.dto;

import br.com.trama.model.enums.PorteConfeccao;
import br.com.trama.model.enums.TipoConfeccao;

public record ConfeccaoResponse(
        Long idConfeccao,
        String nome,
        String cnpj,
        PorteConfeccao porte,
        TipoConfeccao tipo,
        Long idCidade,
        String cidade,
        String uf) {
}
