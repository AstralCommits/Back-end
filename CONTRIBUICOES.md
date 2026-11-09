# Divisão das entregas do Back-end TRAMA

O backend é um único projeto, em um único repositório e com um fluxo sequencial de branches. A divisão abaixo organiza responsabilidades e ordem de execução; não cria projetos ou branches separados por pessoa.

| Responsabilidade | Issues | Quantidade | Participação aproximada | Etapa/branch |
|---|---|---:|---:|---|
| Ester Reis | #1, #2, #3, #4, #12, #13 e #17 | 7 | 64% | Base histórica + Etapa 1 |
| Gabriela Olegário | #11, #14, #15 e #16 | 4 | 36% | Etapa 2 |

## Etapa 1 — Ester Reis

As issues #1 a #4 já foram implementadas no commit histórico `3f56d09`, enviado diretamente à `main`:

- **#1 — BACK-01:** estrutura Spring Boot, Java 21 e Maven.
- **#2 — BACK-02:** entidades JPA e repositórios.
- **#3 — BACK-03:** CRUD de categorias e confecções.
- **#4 — BACK-04:** lançamentos mensais, filtros e procedência do dado.

As próximas entregas atribuídas a esta etapa serão desenvolvidas na branch técnica `feature/backend-regras-importacao`, criada a partir de `develop`:

- **#12 — BACK-08:** tratamento global de erros no formato Problem Details (RFC 7807), incluindo validações, recursos inexistentes, conflitos e erros de integridade.
- **#13 — BACK-09:** importação em lote por CSV, associação ao usuário, validação das linhas, transação e registro do status da importação.
- **#17 — BD:** adequação do backend aos perfis `ADMIN` e `EMPRESARIO`, isolamento por confecção e impacto de cenário entre 0 e 100.

Depois de concluída e integrada a Etapa 1, o trabalho segue na mesma `develop`.

## Etapa 2 — Gabriela Olegário

Gabriela continuará o mesmo repositório a partir da `develop` já atualizada pela Etapa 1, criando a branch técnica `feature/backend-documentacao-qualidade`:

- **#11 — DOC-01:** documentação OpenAPI 3 / Swagger UI e esquema Bearer JWT.
- **#14 — TEST-01:** ampliação dos testes de integração JUnit 5 / MockMvc para autenticação, cadastros, vendas, cenários e importação.
- **#15 — DEVOPS-02:** cobertura com JaCoCo e integração com o Quality Gate institucional do SonarQube.
- **#16 — DEVOPS-01:** workflow GitHub Actions para executar `mvn clean verify` em Pull Requests e pushes para `develop`.

## Regra de continuidade

A Etapa 2 não é um projeto separado nem uma branch paralela. Ela é uma continuação: começa a partir da `develop` depois que o Pull Request da Etapa 1 for integrado. Todas as branches têm nomes relacionados às funcionalidades, e todos os Pull Requests têm como destino a mesma `develop`.

## Registro correto de contribuições

Esta divisão é uma proposta de responsabilidades. O histórico Git deve refletir quem realmente realizou, revisou e publicou cada alteração. Cada integrante deve conferir e compreender as entregas que registrar em sua conta; não devem ser simulados commits ou autoria. Quando uma etapa for feita em conjunto, a colaboração pode ser registrada no Pull Request.
