# Changelog

## [0.1.0] - 2026-10-27

### Adicionado
- Projeto Maven Spring Boot 3.3.13 com Java 21.
- Dependências Spring Web, Data JPA, Validation e Security.
- Drivers JDBC para MySQL, SQLite e H2 para execução local/testes.
- Perfis `local`, `dev` e `sqlite`.
- Entidades JPA `Cidade`, `Confeccao`, `Categoria`, `LancamentoMensal`, `CenarioConcorrencia` e `Usuario`.
- Repositórios Spring Data para entidades centrais.
- CRUD REST para categorias e confecções.
- Cadastro, consulta filtrada, atualização e cancelamento de lançamentos mensais.
- Validação de origem/procedência dos dados e bloqueio de lançamentos duplicados.
- Migração SQL para registrar procedência do dado em lançamentos existentes.
- Tratamento centralizado de erros e testes de integração.
