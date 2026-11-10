package br.com.trama;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.trama.model.Categoria;
import br.com.trama.model.Cidade;
import br.com.trama.model.Confeccao;
import br.com.trama.model.LancamentoMensal;
import br.com.trama.model.Usuario;
import br.com.trama.model.enums.PerfilUsuario;
import br.com.trama.model.enums.OrigemDado;
import br.com.trama.model.enums.OrigemLancamento;
import br.com.trama.model.enums.StatusLancamento;
import br.com.trama.model.enums.PorteConfeccao;
import br.com.trama.model.enums.TipoConfeccao;
import br.com.trama.repository.CategoriaRepository;
import br.com.trama.repository.CidadeRepository;
import br.com.trama.repository.ConfeccaoRepository;
import br.com.trama.repository.ImportacaoRepository;
import br.com.trama.repository.LancamentoMensalRepository;
import br.com.trama.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAndImportacaoIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private CidadeRepository cidadeRepository;
    @Autowired private ConfeccaoRepository confeccaoRepository;
    @Autowired private ImportacaoRepository importacaoRepository;
    @Autowired private LancamentoMensalRepository lancamentoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void loginValidatesPasswordAndReturnsBearerToken() throws Exception {
        String email = "admin-" + UUID.randomUUID() + "@trama.test";
        criarAdmin(email, "SenhaTeste@123");
        String body = objectMapper.writeValueAsString(Map.of("email", email, "senha", "SenhaTeste@123"));
        MvcResult result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty()).andReturn();
        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
        mockMvc.perform(get("/api/categorias").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginRejectsIncorrectPassword() throws Exception {
        String email = "admin-" + UUID.randomUUID() + "@trama.test";
        criarAdmin(email, "SenhaCorreta@123");
        String body = objectMapper.writeValueAsString(Map.of("email", email, "senha", "senha-errada"));
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.detail").value("Usuário ou senha inválidos."));
    }

    @Test
    void csvImportCreatesImportRecordAndMonthlySales() throws Exception {
        String email = "import-" + UUID.randomUUID() + "@trama.test";
        criarAdmin(email, "SenhaTeste@123");
        long categoryId = categoriaRepository.save(new Categoria("CSV-" + UUID.randomUUID())).getIdCategoria();
        Cidade cidade = cidadeRepository.findByNomeIgnoreCaseAndUf("Caruaru", "PE").orElseThrow();
        Confeccao confeccao = new Confeccao();
        confeccao.setNome("Confecção CSV " + UUID.randomUUID());
        confeccao.setPorte(PorteConfeccao.MICRO);
        confeccao.setTipo(TipoConfeccao.CONFECCAO);
        confeccao.setCidade(cidade);
        long confectionId = confeccaoRepository.save(confeccao).getIdConfeccao();
        String token = token(email, "SenhaTeste@123");
        String csv = "mesAno;quantidade;faturamento;idConfeccao;idCategoria;origemDado;nomeFonte\n"
                + "2026-11;42;1250.00;" + confectionId + ";" + categoryId + ";ENTREVISTA;Entrevista de teste\n";
        MockMultipartFile file = new MockMultipartFile("arquivo", "vendas.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart("/api/importacoes").file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.qtdLinhas").value(1));
        org.assertj.core.api.Assertions.assertThat(importacaoRepository.count()).isGreaterThan(0);
    }


    @Test
    void csvImportWithDuplicateRowsRollsBackSalesAndRecordsError() throws Exception {
        String email = "import-erro-" + UUID.randomUUID() + "@trama.test";
        criarAdmin(email, "SenhaTeste@123");
        long categoryId = categoriaRepository.save(new Categoria("CSV-ERRO-" + UUID.randomUUID())).getIdCategoria();
        Cidade cidade = cidadeRepository.findByNomeIgnoreCaseAndUf("Caruaru", "PE").orElseThrow();
        Confeccao confeccao = new Confeccao();
        confeccao.setNome("Confecção CSV ERRO " + UUID.randomUUID());
        confeccao.setPorte(PorteConfeccao.MICRO);
        confeccao.setTipo(TipoConfeccao.CONFECCAO);
        confeccao.setCidade(cidade);
        long confectionId = confeccaoRepository.saveAndFlush(confeccao).getIdConfeccao();
        long salesBefore = lancamentoRepository.count();
        String token = token(email, "SenhaTeste@123");
        String csv = "mesAno;quantidade;faturamento;idConfeccao;idCategoria;origemDado;nomeFonte\n"
                + "2026-11;42;1250.00;" + confectionId + ";" + categoryId + ";ENTREVISTA;Entrevista\n"
                + "2026-11;18;500.00;" + confectionId + ";" + categoryId + ";QUESTIONARIO;Questionário\n";
        MockMultipartFile file = new MockMultipartFile("arquivo", "vendas-duplicadas.csv", "text/csv",
                csv.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/importacoes").file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        org.assertj.core.api.Assertions.assertThat(lancamentoRepository.count()).isEqualTo(salesBefore);
        org.assertj.core.api.Assertions.assertThat(importacaoRepository.findAll().stream()
                .anyMatch(i -> i.getNomeArquivo().equals("vendas-duplicadas.csv")
                        && i.getStatus().name().equals("ERRO"))).isTrue();
    }

    @Test
    void empresarioProfileUsesUpdatedRoleAndIsLimitedToItsConfection() throws Exception {
        String email = "empresario-" + UUID.randomUUID() + "@trama.test";
        String senha = "SenhaTeste@123";
        Confeccao propria = criarConfeccao("Própria");
        Confeccao outra = criarConfeccao("Outra");
        Usuario empresario = new Usuario();
        empresario.setNome("Empresário de teste");
        empresario.setEmail(email);
        empresario.setSenhaHash(passwordEncoder.encode(senha));
        empresario.setPerfil(PerfilUsuario.EMPRESARIO);
        empresario.setConfeccao(propria);
        usuarioRepository.saveAndFlush(empresario);
        String token = token(email, senha);
        long categoriaId = categoriaRepository.save(new Categoria("Empresario-" + UUID.randomUUID())).getIdCategoria();
        Map<String, Object> vendaPropria = Map.of("idConfeccao", propria.getIdConfeccao(),
                "idCategoria", categoriaId, "mesAno", "2026-11", "quantidade", 5,
                "faturamento", new BigDecimal("500.00"), "origem", "MANUAL",
                "origemDado", "ENTREVISTA", "nomeFonte", "Entrevista");
        mockMvc.perform(post("/api/vendas").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(vendaPropria)))
                .andExpect(status().isCreated());
        Map<String, Object> vendaOutra = Map.of("idConfeccao", outra.getIdConfeccao(),
                "idCategoria", categoriaId, "mesAno", "2026-12", "quantidade", 5,
                "faturamento", new BigDecimal("500.00"), "origem", "MANUAL",
                "origemDado", "ENTREVISTA", "nomeFonte", "Entrevista");
        mockMvc.perform(post("/api/vendas").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(vendaOutra)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/vendas").header("Authorization", "Bearer " + token)
                        .param("confeccao", String.valueOf(outra.getIdConfeccao())))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].idConfeccao").value(propria.getIdConfeccao()));
        mockMvc.perform(get("/api/confeccoes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idConfeccao").value(propria.getIdConfeccao()));
        mockMvc.perform(get("/api/confeccoes/{id}", outra.getIdConfeccao())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        LancamentoMensal vendaDeOutraConfeccao = new LancamentoMensal();
        vendaDeOutraConfeccao.setMesReferencia(LocalDate.of(2026, 11, 1));
        vendaDeOutraConfeccao.setQuantidade(20);
        vendaDeOutraConfeccao.setFaturamento(new BigDecimal("1500.00"));
        vendaDeOutraConfeccao.setOrigem(OrigemLancamento.MANUAL);
        vendaDeOutraConfeccao.setStatus(StatusLancamento.ATIVO);
        vendaDeOutraConfeccao.setConfeccao(outra);
        vendaDeOutraConfeccao.setCategoria(categoriaRepository.findById(categoriaId).orElseThrow());
        vendaDeOutraConfeccao.setOrigemDado(OrigemDado.ENTREVISTA);
        vendaDeOutraConfeccao.setNomeFonte("Entrevista de outra confecção");
        lancamentoRepository.saveAndFlush(vendaDeOutraConfeccao);

        mockMvc.perform(get("/api/indicadores").header("Authorization", "Bearer " + token)
                        .param("inicio", "2026-11").param("fim", "2026-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faturamentoTotal").value(500.00));
    }

    private Confeccao criarConfeccao(String prefixo) {
        Cidade cidade = cidadeRepository.findByNomeIgnoreCaseAndUf("Caruaru", "PE").orElseThrow();
        Confeccao confeccao = new Confeccao();
        confeccao.setNome(prefixo + " " + UUID.randomUUID());
        confeccao.setPorte(PorteConfeccao.MICRO);
        confeccao.setTipo(TipoConfeccao.CONFECCAO);
        confeccao.setCidade(cidade);
        return confeccaoRepository.saveAndFlush(confeccao);
    }

    private void criarAdmin(String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setNome("Administrador de teste");
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuarioRepository.saveAndFlush(usuario);
    }

    private String token(String email, String senha) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("email", email, "senha", senha));
        MvcResult result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
