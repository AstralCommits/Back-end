# TRAMA | Backend — Etapa 1

Esta é a primeira etapa de atualização do backend único do TRAMA. Ela parte do histórico existente das issues #1 a #4 e acrescenta as entregas das issues #12, #13 e #17.

## Issues desta etapa

- **#1 a #4:** estrutura Spring Boot, entidades JPA, CRUD de categorias/confecções e lançamentos mensais. Já constam no commit histórico `3f56d09` da branch `main`.
- **#12 — BACK-08:** tratamento global de erros com Problem Details (RFC 7807).
- **#13 — BACK-09:** importação de vendas por CSV com validação, vínculo ao usuário e controle transacional.
- **#17:** compatibilidade com os perfis `ADMIN` e `EMPRESARIO`, isolamento por confecção e impacto do cenário entre 0 e 100.

## Aplicação no GitHub

1. Crie a branch `develop` a partir da `main`, preservando o histórico.
2. Crie `feature/backend-regras-importacao` a partir da `develop`.
3. Copie o conteúdo deste pacote para a raiz do repositório clonado, preservando `.git`.
4. Revise, valide e registre os commits reais desta etapa.
5. Abra um Pull Request para `develop`.

A próxima etapa será aplicada somente depois que este Pull Request for integrado. O nome da branch descreve as funcionalidades, não a pessoa que trabalha nelas.

## Alinhamento com o Banco de Dados

A `develop` do Banco de Dados usa os perfis `ADMIN`/`EMPRESARIO`, `chk_cenario_impacto` e a coluna `fonte_dado`. Para cumprir RF04, este backend também prevê armazenar `nome_fonte`; a migração `V0_5_0__nome_fonte_lancamento.sql` precisa de aprovação/alinhamento da equipe de BD antes de ser aplicada no MySQL. Não execute `V0_4_0` caso o PR #6 já esteja aplicado.

## Execução local

Requer Java 21. O perfil padrão `local` utiliza H2 em memória. No Windows, execute `mvnw.cmd clean test` e `mvnw.cmd spring-boot:run`; em Linux/macOS, execute `./mvnw clean test` e `./mvnw spring-boot:run`.

Este pacote não inclui a sobreposição da Etapa 2 (OpenAPI, testes adicionais, JaCoCo/SonarQube e CI). Ela será aplicada em uma branch criada a partir da `develop` já atualizada.
