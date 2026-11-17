# Issue 01 — Documentação OpenAPI / Swagger

## Arquivos desta entrega
- `pom.xml`: adiciona SpringDoc OpenAPI.
- `src/main/java/br/com/trama/config/OpenApiConfig.java`: configura título da API e autenticação Bearer JWT.

## Como aplicar
1. Copie os arquivos mantendo os caminhos dentro do projeto.
2. No `pom.xml`, confira a dependência `springdoc-openapi-starter-webmvc-ui` (não duplique se já existir).
3. Os controllers e DTOs de artesãos, produtos e cotações recebem as anotações `@Operation`, `@Tag`, `@Schema` e exemplos nas entregas das Issues 02 e 04. Aplique esta issue depois delas para documentar todas as rotas.
4. Execute a aplicação e acesse `/swagger-ui.html`.

**Dependência:** Issues 02 e 04 para que as rotas de artesãos, produtos e cotações estejam presentes e documentadas.
