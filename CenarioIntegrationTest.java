package br.com.trama;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.trama.model.Categoria;
import br.com.trama.model.Cidade;
import br.com.trama.model.Confeccao;
import br.com.trama.model.LancamentoMensal;
import br.com.trama.model.Usuario;
import br.com.trama.model.enums.OrigemDado;
import br.com.trama.model.enums.OrigemLancamento;
import br.com.trama.model.enums.PerfilUsuario;
import br.com.trama.model.enums.PorteConfeccao;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.model.enums.TipoConfeccao;
import br.com.trama.repository.CategoriaRepository;
import br.com.trama.repository.CidadeRepository;
import br.com.trama.repository.ConfeccaoRepository;
import br.com.trama.repository.LancamentoMensalRepository;
import br.com.trama.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CenarioIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private CidadeRepository cidadeRepository;
    @Autowired private ConfeccaoRepository confeccaoRepository;
    @Autowired private LancamentoMensalRepository lancamentoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void scenarioCalculatesImpactFromPersistedSalesAndRejectsAboveOneHundred() throws Exception {
        String email = "cenario-" + UUID.randomUUID() + "@trama.test";
        Usuario usuario = new Usuario();
        usuario.setNome("Administrador cenário");
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode("SenhaTeste@123"));
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuarioRepository.saveAndFlush(usuario);
        Categoria categoria = categoriaRepository.save(new Categoria("Cenario-" + UUID.randomUUID()));
        Cidade cidade = cidadeRepository.findByNomeIgnoreCaseAndUf("Toritama", "PE").orElseThrow();
        Confeccao confeccao = new Confeccao();
        confeccao.setNome("Confecção cenário " + UUID.randomUUID());
        confeccao.setPorte(PorteConfeccao.MICRO);
        confeccao.setTipo(TipoConfeccao.CONFECCAO);
        confeccao.setCidade(cidade);
        confeccao = confeccaoRepository.saveAndFlush(confeccao);
        LancamentoMensal venda = new LancamentoMensal();
        venda.setMesReferencia(LocalDate.of(2026, 11, 1));
        venda.setQuantidade(10);
        venda.setFaturamento(new BigDecimal("1000.00"));
        venda.setOrigem(OrigemLancamento.MANUAL);
        venda.setStatus(StatusLancamento.ATIVO);
        venda.setConfeccao(confeccao);
        venda.setCategoria(categoria);
        venda.setOrigemDado(OrigemDado.ENTREVISTA);
        venda.setNomeFonte("Entrevista de teste");
        lancamentoRepository.saveAndFlush(venda);
        String token = login(email);
        String request = objectMapper.writeValueAsString(Map.of("categoriaId", categoria.getIdCategoria(),
                "percentualImpacto", new BigDecimal("10.00")));
        mockMvc.perform(post("/api/cenarios/simular").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faturamentoReal").value(1000.00))
                .andExpect(jsonPath("$.faturamentoHipotetico").value(900.00));
        String invalid = objectMapper.writeValueAsString(Map.of("categoriaId", categoria.getIdCategoria(),
                "percentualImpacto", new BigDecimal("100.01")));
        mockMvc.perform(post("/api/cenarios/simular").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest());
    }

    private String login(String email) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("email", email, "senha", "SenhaTeste@123"));
        MvcResult result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
