package br.com.trama;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.trama.dto.ComissaoResponse;
import br.com.trama.error.BadRequestException;
import br.com.trama.service.ComissaoService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testes unitários da regra de comissão (90% confecção / 10% plataforma).
 * O serviço é puro (sem repositórios nem outros beans), então não há o que simular com Mockito.
 */
class ComissaoServiceTest {
    private final ComissaoService service = new ComissaoService();

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private static void assertRateio(ComissaoResponse r, String total, String repasse, String taxa) {
        assertEquals(bd(total), r.valorTotal(), "valor total");
        assertEquals(bd(repasse), r.repasseConfeccao(), "repasse da confecção");
        assertEquals(bd(taxa), r.taxaPlataforma(), "taxa da plataforma");
    }

    @Test
    void encomendaDeCincoMilReaisRepassaExatamente4500E500DeTaxaSemPerdas() {
        ComissaoResponse r = service.calcular(bd("5000.00"));
        assertRateio(r, "5000.00", "4500.00", "500.00");
        assertEquals(0, r.repasseConfeccao().add(r.taxaPlataforma()).compareTo(r.valorTotal()));
    }

    @ParameterizedTest(name = "pedido simples {0} -> repasse {1}, taxa {2}")
    @CsvSource({
            "100.00,   90.00,  10.00",
            "59.90,    53.91,   5.99",
            "1.00,      0.90,   0.10",
            "0.01,      0.01,   0.00"
    })
    void pedidosSimples(String valor, String repasse, String taxa) {
        assertRateio(service.calcular(bd(valor)), valor, repasse, taxa);
    }

    @ParameterizedTest(name = "encomenda volumosa {0} -> repasse {1}, taxa {2}")
    @CsvSource({
            "1000000.00,     900000.00,    100000.00",
            "12345678.90,  11111111.01,   1234567.89",
            "999999999.99, 899999999.99,  100000000.00"
    })
    void encomendasCorporativasVolumosas(String valor, String repasse, String taxa) {
        assertRateio(service.calcular(bd(valor)), valor, repasse, taxa);
    }

    @ParameterizedTest(name = "arredondamento bancário: {0} -> taxa {2}")
    @CsvSource({
            // 10% = 0,005 / 0,015 / 0,025 / 0,035: empate sempre vai para o centavo par
            "0.05,  0.05, 0.00",
            "0.15,  0.13, 0.02",
            "0.25,  0.23, 0.02",
            "0.35,  0.31, 0.04"
    })
    void taxaUsaArredondamentoHalfEven(String valor, String repasse, String taxa) {
        assertRateio(service.calcular(bd(valor)), valor, repasse, taxa);
    }

    @ParameterizedTest(name = "entrada com mais de 2 casas: {0} -> total {1}")
    @CsvSource({
            "100.005, 100.00, 90.00, 10.00",
            "100.015, 100.02, 90.02, 10.00",
            "100.004, 100.00, 90.00, 10.00",
            "100.006, 100.01, 90.01, 10.00"
    })
    void entradaComMaisDeDuasCasasENormalizadaComHalfEven(String entrada, String total, String repasse, String taxa) {
        assertRateio(service.calcular(bd(entrada)), total, repasse, taxa);
    }

    @Test
    void valorSemCasasDecimaisGeraResultadoComDuasCasas() {
        assertRateio(service.calcular(bd("5000")), "5000.00", "4500.00", "500.00");
    }

    @Test
    void valorZeroGeraRepasseETaxaZerados() {
        assertRateio(service.calcular(BigDecimal.ZERO), "0.00", "0.00", "0.00");
    }

    @Test
    void repasseMaisTaxaSempreIgualAoTotalEmTodosOsCentavosDeZeroACemReais() {
        for (int centavos = 0; centavos <= 10_000; centavos++) {
            BigDecimal valor = BigDecimal.valueOf(centavos, 2);
            ComissaoResponse r = service.calcular(valor);
            assertEquals(valor, r.repasseConfeccao().add(r.taxaPlataforma()),
                    "soma diferente do total para " + valor);
        }
    }

    @Test
    void valorNuloEhRejeitado() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.calcular(null));
        assertEquals("O valor do pedido é obrigatório.", ex.getMessage());
    }

    @Test
    void valorNegativoEhRejeitado() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.calcular(bd("-0.01")));
        assertEquals("O valor do pedido não pode ser negativo.", ex.getMessage());
    }
}
