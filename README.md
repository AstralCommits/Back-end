# TRAMA | Backend — Etapa 1

Esta branch reúne a primeira etapa de desenvolvimento e atualização do backend do TRAMA. Seu objetivo é concentrar as funcionalidades relacionadas à estrutura principal da aplicação, ao gerenciamento de dados e às regras de negócio necessárias para o funcionamento da plataforma.

A branch `backend-regras-importacao` foi criada a partir da `develop` e contém as implementações das issues #1 a #4, #12, #13 e #17.

## Issues desta etapa

- **#1 a #4:** configuração inicial do projeto Spring Boot, criação das entidades e repositórios JPA, gerenciamento de categorias e confecções e operações relacionadas aos lançamentos mensais.
- **#12 — BACK-08:** implementação do tratamento global de exceções, utilizando Problem Details conforme a RFC 7807, para padronizar as respostas de erro da API.
- **#13 — BACK-09:** desenvolvimento da importação de dados de vendas por arquivos CSV, incluindo validação dos registros, associação ao usuário responsável e controle transacional.
- **#17:** atualização das regras de usuários e perfis, com suporte aos perfis `ADMIN` e `EMPRESARIO`, controle de acesso aos dados por confecção e validação do impacto dos cenários entre 0 e 100.

## Organização das branches

O repositório utiliza branches para organizar o desenvolvimento e manter a versão principal do projeto estável.

- **`main`:** mantém a versão estável e consolidada do backend.
- **`develop`:** reúne as funcionalidades integradas e validadas pela equipe durante o desenvolvimento.
- **`backend-regras-importacao`:** concentra as implementações da primeira etapa do backend, antes de serem integradas à `develop`.

As alterações desta branch devem ser revisadas e integradas à `develop` por meio de um Pull Request. A próxima etapa será desenvolvida em uma nova branch, criada a partir da `develop` atualizada.

## Alinhamento com o Banco de Dados

Esta etapa considera a estrutura e as regras definidas pela equipe de Banco de Dados, incluindo os perfis `ADMIN` e `EMPRESARIO`, a restrição `chk_cenario_impacto` e os campos relacionados à origem dos dados.

O backend também prevê o armazenamento do campo `nome_fonte`, conforme a necessidade de identificação da origem dos lançamentos. A migração `V0_5_0__nome_fonte_lancamento.sql` deverá ser alinhada e aprovada pela equipe de Banco de Dados antes de sua aplicação no MySQL.

A migração `V0_4_0` não deve ser executada caso as alterações correspondentes ao PR #6 já estejam aplicadas no banco.

## Execução local

O projeto utiliza Java 21 e Maven. O perfil padrão `local` utiliza o banco H2 em memória para facilitar a execução e os testes durante o desenvolvimento.

No Windows:

```bash
mvnw.cmd clean test
mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

## Próxima etapa

Esta branch corresponde à primeira etapa de desenvolvimento do backend. Após sua revisão e integração à `develop`, será iniciada a segunda etapa, contemplando a documentação da API com OpenAPI/Swagger, testes automatizados adicionais, análise de cobertura com JaCoCo, integração com SonarQube e configuração de CI com GitHub Actions.

Essas entregas serão realizadas em uma nova branch, baseada na `develop` atualizada, mantendo a continuidade do desenvolvimento e a organização do histórico do projeto.
