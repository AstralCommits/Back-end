# Rascunhos de comentários para as issues

Substitua `[HASH]` e `[LINK-DO-COMMIT]` pelos dados reais de cada commit depois que ele existir. Os nomes das branches abaixo são técnicos e seguem a sequência única de desenvolvimento. Não declare CI ou Quality Gate aprovado sem uma execução real.

## #1 — BACK-01
✅ Concluída no commit [3f56d09](https://github.com/AstralCommits/Back-end/commit/3f56d0987525f575d85d1a8c97b9280d9bce87dd) (branch `main`). O projeto foi configurado com Spring Boot 3.3.13, Maven e Java 21. A aplicação iniciou corretamente na porta 8080 e os 7 testes automatizados da entrega original passaram localmente.

## #2 — BACK-02
✅ Implementada no commit [3f56d09](https://github.com/AstralCommits/Back-end/commit/3f56d0987525f575d85d1a8c97b9280d9bce87dd) (branch `main`). Foram mapeadas as entidades JPA centrais do backend e criados os repositórios Spring Data JPA correspondentes.

## #3 — BACK-03
✅ Implementada no commit [3f56d09](https://github.com/AstralCommits/Back-end/commit/3f56d0987525f575d85d1a8c97b9280d9bce87dd) (branch `main`). Foram desenvolvidos os endpoints REST de categorias e confecções, com DTOs e validações, incluindo as operações de cadastro, consulta, alteração e exclusão.

## #4 — BACK-04
✅ Implementada no commit [3f56d09](https://github.com/AstralCommits/Back-end/commit/3f56d0987525f575d85d1a8c97b9280d9bce87dd) (branch `main`). Foram desenvolvidos o cadastro e a consulta de lançamentos mensais, com filtros, validações, bloqueio de duplicidade e retorno da procedência dos dados. O tipo de fonte é mapeado para `fonte_dado`; o nome da fonte utiliza a coluna complementar `nome_fonte`, cuja migração precisa ser alinhada ao schema SQL da equipe.

## #11 — DOC-01
✅ Implementada no commit [HASH](LINK-DO-COMMIT) (branch `feature/backend-documentacao-qualidade`). Foi adicionada a documentação OpenAPI 3 com Swagger UI, incluindo esquema de autenticação HTTP Bearer JWT para explorar os endpoints protegidos.

## #12 — BACK-08
✅ Implementada no commit [HASH](LINK-DO-COMMIT) (branch `feature/backend-regras-importacao`). O tratamento global foi padronizado com Problem Details (RFC 7807), contemplando validações, recursos não encontrados, conflitos e violações de integridade do banco, com mensagens de erro em JSON.

## #13 — BACK-09
✅ Implementada no commit [HASH](LINK-DO-COMMIT) (branch `feature/backend-regras-importacao`). Foi criado o endpoint `POST /api/importacoes` para envio de CSV, com validação das colunas e linhas, associação ao usuário autenticado e registro do status na tabela `importacao`. A gravação dos lançamentos é transacional: arquivos com linhas inválidas ou duplicadas não deixam lançamentos parcialmente persistidos.

## #14 — TEST-01
✅ Implementada no commit [HASH](LINK-DO-COMMIT) (branch `feature/backend-documentacao-qualidade`). Foi ampliada a suíte JUnit 5/MockMvc para cobrir autenticação JWT, cadastros, validações, lançamentos, isolamento por confecção, importação CSV e cálculo de cenários. A correspondência exata com todas as US-01 a US-06 ainda deve ser conferida com o SRS completo.

## #15 — DEVOPS-02
✅ Implementada no commit [HASH](LINK-DO-COMMIT) (branch `feature/backend-documentacao-qualidade`). Foi configurado o JaCoCo para gerar os relatórios HTML/XML de cobertura e o plugin Maven do SonarQube para enviar a análise ao servidor institucional quando as credenciais e variáveis do projeto estiverem configuradas. O Quality Gate depende de execução no servidor institucional.

## #16 — DEVOPS-01
✅ Implementada no commit [HASH](LINK-DO-COMMIT) (branch `feature/backend-documentacao-qualidade`). Foi criado o workflow do GitHub Actions para configurar Java 21 e executar `mvn clean verify` em pushes e Pull Requests direcionados à `develop`. A aprovação do CI depende da execução real do workflow no GitHub.

## #17 — BD
✅ Implementação de compatibilidade incluída no commit [HASH](LINK-DO-COMMIT) (branch `feature/backend-regras-importacao`). O backend foi atualizado para os perfis `ADMIN` e `EMPRESARIO`, com restrição dos dados do empresário à própria confecção e validação do impacto de cenário entre 0 e 100. Incluída migração de compatibilidade para bancos que ainda utilizam o perfil antigo; a migração não deve ser executada caso o schema atualizado da equipe já esteja aplicado.
