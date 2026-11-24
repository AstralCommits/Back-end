package br.com.trama.service;

import br.com.trama.dto.ComissaoResponse;
import br.com.trama.error.BadRequestException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

/**
 * Regra de impacto social: 90% do valor aprovado vai para a confecção e 10% para a manutenção
 * da plataforma TRAMA (taxa operacional).
 *
 * <p>A taxa é calculada com arredondamento bancário (HALF_EVEN) em 2 casas e o repasse é o restante
 * (total − taxa). Assim a soma dos dois é sempre exatamente o total, sem perda de centavos.
 */
@Service
public class ComissaoService {
    private static final int CASAS = 2;
    private static final BigDecimal PERCENTUAL_PLATAFORMA = new BigDecimal("0.10");

    public ComissaoResponse calcular(BigDecimal valorAprovado) {
        if (valorAprovado == null) {
            throw new BadRequestException("O valor do pedido é obrigatório.");
        }
        if (valorAprovado.signum() < 0) {
            throw new BadRequestException("O valor do pedido não pode ser negativo.");
        }
        BigDecimal total = valorAprovado.setScale(CASAS, RoundingMode.HALF_EVEN);
        BigDecimal taxa = total.multiply(PERCENTUAL_PLATAFORMA).setScale(CASAS, RoundingMode.HALF_EVEN);
        BigDecimal repasse = total.subtract(taxa);
        return new ComissaoResponse(total, repasse, taxa);
    }
}
