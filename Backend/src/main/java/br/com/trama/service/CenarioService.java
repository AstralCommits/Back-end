package br.com.trama.service;

import br.com.trama.dto.SimulacaoCenarioRequest;
import br.com.trama.dto.SimulacaoCenarioResponse;
import br.com.trama.error.BadRequestException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class CenarioService {

    public SimulacaoCenarioResponse simular(
            SimulacaoCenarioRequest request,
            BigDecimal faturamentoReal) {

        BigDecimal percentual = request.percentualImpacto();

        if (percentual.compareTo(BigDecimal.ZERO) < 0
                || percentual.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException(
                    "Percentual deve estar entre 0 e 100.");
        }

        BigDecimal fator = BigDecimal.ONE.subtract(
                percentual.divide(
                        BigDecimal.valueOf(100),
                        4,
                        RoundingMode.HALF_UP));

        BigDecimal faturamentoHipotetico =
                faturamentoReal.multiply(fator);

        BigDecimal diferencaReais =
                faturamentoReal.subtract(faturamentoHipotetico);

        return new SimulacaoCenarioResponse(
                faturamentoReal,
                faturamentoHipotetico,
                diferencaReais,
                percentual
        );
    }
}
