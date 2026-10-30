package br.com.trama;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void healthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void categoryDuplicateIsRejectedWithExpectedMessage() throws Exception {
        String name = "Teste-" + UUID.randomUUID().toString().substring(0, 8);
        String body = objectMapper.writeValueAsString(Map.of("nome", name));

        mockMvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Categoria já cadastrada"));
    }


    @Test
    void categoryCrudSupportsReadUpdateAndDelete() throws Exception {
        String name = "CRUD-" + UUID.randomUUID().toString().substring(0, 8);
        String createJson = objectMapper.writeValueAsString(Map.of("nome", name));
        String created = mockMvc.perform(post("/api/categorias").contentType(MediaType.APPLICATION_JSON).content(createJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("idCategoria").asLong();

        mockMvc.perform(get("/api/categorias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(name));

        String updated = objectMapper.writeValueAsString(Map.of("nome", name + "-alterada"));
        mockMvc.perform(put("/api/categorias/{id}", id).contentType(MediaType.APPLICATION_JSON).content(updated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(name + "-alterada"));

        mockMvc.perform(delete("/api/categorias/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/categorias/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void confectionCanBeCreatedReadUpdatedAndDeleted() throws Exception {
        String name = "Confeccao-" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> request = Map.of(
                "nome", name,
                "porte", "MICRO",
                "tipo", "CONFECCAO",
                "cidade", "Santa Cruz do Capibaribe");
        String created = mockMvc.perform(post("/api/confeccoes").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("idConfeccao").asLong();

        mockMvc.perform(get("/api/confeccoes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("Santa Cruz do Capibaribe"));

        Map<String, Object> update = Map.of(
                "nome", name + " Atualizada",
                "porte", "PEQUENA",
                "tipo", "CONFECCAO",
                "cidade", "Toritama");
        mockMvc.perform(put("/api/confeccoes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("Toritama"));

        mockMvc.perform(delete("/api/confeccoes/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/confeccoes/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void confectionRejectsCityOutsideApprovedAgresteList() throws Exception {
        Map<String, Object> request = Map.of(
                "nome", "Confecção " + UUID.randomUUID(),
                "porte", "PEQUENA",
                "tipo", "CONFECCAO",
                "cidade", "Recife");
        mockMvc.perform(post("/api/confeccoes").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Cidade inválida. Escolha Toritama, Caruaru ou Santa Cruz do Capibaribe."));
    }

    @Test
    void saleCreationReturnsSourceProvenanceAndDuplicateIsRejected() throws Exception {
        String categoryName = "Teste-" + UUID.randomUUID().toString().substring(0, 8);
        String categoryJson = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("nome", categoryName))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long categoryId = objectMapper.readTree(categoryJson).get("idCategoria").asLong();

        Map<String, Object> confectionRequest = Map.of(
                "nome", "Confecção teste " + UUID.randomUUID(),
                "porte", "PEQUENA",
                "tipo", "CONFECCAO",
                "cidade", "Caruaru");
        String confectionJson = mockMvc.perform(post("/api/confeccoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confectionRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long confectionId = objectMapper.readTree(confectionJson).get("idConfeccao").asLong();

        Map<String, Object> sale = Map.of(
                "idConfeccao", confectionId,
                "idCategoria", categoryId,
                "mesAno", "2026-10",
                "quantidade", 100,
                "faturamento", new BigDecimal("2500.00"),
                "origem", "MANUAL",
                "origemDado", "FONTE_SECUNDARIA",
                "nomeFonte", "Relatório de teste");
        String saleJson = objectMapper.writeValueAsString(sale);

        mockMvc.perform(post("/api/vendas").contentType(MediaType.APPLICATION_JSON).content(saleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mesAno").value("2026-10"))
                .andExpect(jsonPath("$.origemDado").value("FONTE_SECUNDARIA"))
                .andExpect(jsonPath("$.nomeFonte").value("Relatório de teste"));

        mockMvc.perform(post("/api/vendas").contentType(MediaType.APPLICATION_JSON).content(saleJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Já existe um lançamento para esta confecção, categoria e mês de referência."));

        mockMvc.perform(get("/api/vendas")
                        .param("categoria", String.valueOf(categoryId))
                        .param("confeccao", String.valueOf(confectionId))
                        .param("mesAno", "2026-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeFonte").value("Relatório de teste"));
    }

    @Test
    void negativeSaleQuantityIsRejected() throws Exception {
        Map<String, Object> request = Map.of(
                "idConfeccao", 1,
                "idCategoria", 1,
                "mesAno", "2026-09",
                "quantidade", -2,
                "faturamento", new BigDecimal("10.00"),
                "origem", "MANUAL",
                "origemDado", "ENTREVISTA",
                "nomeFonte", "Entrevista de teste");
        mockMvc.perform(post("/api/vendas").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
