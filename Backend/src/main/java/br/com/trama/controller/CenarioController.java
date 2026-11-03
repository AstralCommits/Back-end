package br.com.trama.controller;

import br.com.trama.dto.SimulacaoCenarioRequest;
import br.com.trama.dto.SimulacaoCenarioResponse;
import br.com.trama.service.CenarioService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cenarios")
public class CenarioController {

    private final CenarioService service;

    public CenarioController(CenarioService service) {
        this.service = service;
    }

    @PostMapping("/simular")
    public SimulacaoCenarioResponse simular(
            @Valid @RequestBody SimulacaoCenarioRequest request) {

        /*
         * Valor provisório até integrar com os lançamentos
         * reais do banco.
         */
        BigDecimal faturamentoReal = BigDecimal.valueOf(10000);

        return service.simular(request, faturamentoReal);
    }
}
