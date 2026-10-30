# TRAMA Backend

API REST do projeto **TRAMA — Tecendo Relações e Analisando o Mercado do Agreste**. Este repositório implementa as issues BACK-01 a BACK-04 com Java 21, Spring Boot 3.3.x, Maven, Spring Web, Spring Data JPA, Bean Validation, Spring Security e drivers para MySQL e SQLite.

## O que está implementado

- Inicialização Spring Boot na porta `8080`.
- Perfil `local` com H2 em memória para executar e testar a API sem conectar o banco MySQL da equipe.
- Perfil `dev` para o banco MySQL `confeccoes` existente.
- Perfil `sqlite` para um arquivo SQLite configurável.
- Entidades JPA: `Cidade`, `Confeccao`, `Categoria`, `LancamentoMensal`, `CenarioConcorrencia` e `Usuario`.
- Repositórios `JpaRepository` para as entidades centrais.
- CRUD REST de categorias e confecções.
- Cadastro, consulta com filtros, atualização e cancelamento de lançamentos mensais.
- Validações para categorias duplicadas, cidades autorizadas, campos obrigatórios, mês `AAAA-MM`, quantidades e valores não negativos.
- Bloqueio de lançamento duplicado para a mesma confecção/categoria/mês.
- Procedência do dado devolvida no JSON por `origemDado` e `nomeFonte`.
- Tratamento consistente de erros em JSON.
- Testes de integração com MockMvc.

## Atenção à segurança

A dependência Spring Security está incluída, mas a configuração de segurança da API está deliberadamente aberta para facilitar integração e testes destas quatro issues. **Esta versão não deve ser publicada em produção com dados reais antes de implementar autenticação/autorização da API e isolamento por confecção.** Não utilize contas ou senhas de demonstração para contornar isso.

## Requisitos

- Java 21.
- Maven 3.9+ (ou utilizar `./mvnw`, que tenta usar Maven instalado e, se necessário, baixar Maven 3.9.9).
- MySQL 8 para o perfil `dev`, se for usar o banco da equipe.

## Executar localmente sem alterar o banco da equipe

O perfil padrão é `local`, que utiliza um H2 em memória. Ele é adequado para testar endpoints, não é armazenamento persistente e é reiniciado quando a aplicação é encerrada.

Linux/macOS:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd clean test
mvnw.cmd spring-boot:run
```

A API será iniciada em `http://localhost:8080`.

Verificação de saúde:

```bash
curl http://localhost:8080/api/health
```

Resposta esperada:

```json
{"status":"UP","application":"TRAMA Backend"}
```

O perfil local cria as três cidades permitidas e cinco categorias iniciais caso ainda não existam. Ele não cria confecções, usuários ou vendas fictícias automaticamente.

## Conectar ao MySQL já existente

**Não execute um script que comece com `DROP DATABASE` em um banco que contenha dados a preservar.** O script completo fornecido pela equipe recria o banco `confeccoes`; utilize-o somente em ambiente descartável quando tiver certeza de que deseja apagar os dados.

1. Confirme que o banco `confeccoes` já existe e que o schema da equipe foi aplicado.
2. Execute uma única vez a migração `database/migrations/V0_3_2__origem_dado_lancamento.sql` no banco existente. Ela acrescenta a procedência do dado sem recriar as tabelas.
3. Configure as variáveis de ambiente:

```bash
export DB_URL='jdbc:mysql://localhost:3306/confeccoes?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Recife&characterEncoding=UTF-8'
export DB_USERNAME='seu_usuario'
export DB_PASSWORD='sua_senha'
```

4. Inicie o perfil MySQL:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

O perfil `dev` usa `ddl-auto: validate`, ou seja, valida o schema existente sem alterar automaticamente as tabelas.

## Usar SQLite

O driver e o dialeto do SQLite estão incluídos. Para iniciar com um arquivo local:

```bash
export SQLITE_URL='jdbc:sqlite:./data/confeccoes.db'
./mvnw spring-boot:run -Dspring-boot.run.profiles=sqlite
```

O diretório do arquivo precisa existir. O perfil SQLite permite evolução inicial do schema via Hibernate, mas a migração MySQL fornecida não se aplica automaticamente ao SQLite.

## Endpoints

### Saúde

- `GET /api/health`

### Cidades permitidas

- `GET /api/cidades`

Retorna apenas Toritama, Caruaru e Santa Cruz do Capibaribe (PE), usadas para cadastrar novas confecções.

### Categorias

- `GET /api/categorias`
- `GET /api/categorias/{id}`
- `POST /api/categorias`
- `PUT /api/categorias/{id}`
- `DELETE /api/categorias/{id}`

Exemplo de criação:

```bash
curl -i -X POST http://localhost:8080/api/categorias \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Jeans"}'
```

Nome é obrigatório e tem limite de 50 caracteres. Duplicatas (ignorando maiúsculas/minúsculas) retornam HTTP 400 com `Categoria já cadastrada`.

### Confecções

- `GET /api/confeccoes`
- `GET /api/confeccoes/{id}`
- `POST /api/confeccoes`
- `PUT /api/confeccoes/{id}`
- `DELETE /api/confeccoes/{id}`

Exemplo:

```bash
curl -i -X POST http://localhost:8080/api/confeccoes \
  -H 'Content-Type: application/json' \
  -d '{"nome":"Confecção Agreste","cnpj":null,"porte":"PEQUENA","tipo":"CONFECCAO","cidade":"Caruaru"}'
```

`cidade` aceita apenas `Toritama`, `Caruaru` ou `Santa Cruz do Capibaribe`, ignorando maiúsculas/minúsculas e acentuação. `porte` aceita `MICRO`, `PEQUENA` ou `MEDIA`; `tipo` aceita `CONFECCAO` ou `FACCAO`.

### Vendas mensais

- `POST /api/vendas`
- `GET /api/vendas?categoria={id}&confeccao={id}&mesAno=AAAA-MM`
- `GET /api/vendas/{id}`
- `PUT /api/vendas/{id}`
- `DELETE /api/vendas/{id}` — cancela logicamente o lançamento (`lanc_status=CANCELADO`).

Exemplo (crie categoria e confecção antes e substitua os IDs):

```bash
curl -i -X POST http://localhost:8080/api/vendas \
  -H 'Content-Type: application/json' \
  -d '{
    "idConfeccao": 1,
    "idCategoria": 1,
    "mesAno": "2026-10",
    "quantidade": 120,
    "faturamento": 3600.00,
    "origem": "MANUAL",
    "origemDado": "FONTE_SECUNDARIA",
    "nomeFonte": "Relatório setorial identificado"
  }'
```

Campos importantes:

- `mesAno`: formato `AAAA-MM`; o backend grava o primeiro dia do mês em `mes_referencia`.
- `quantidade`: inteiro maior ou igual a zero.
- `faturamento`: número decimal maior ou igual a zero.
- `origem`: método de inclusão (`MANUAL` ou `IMPORTACAO`), compatível com a tabela atual.
- `origemDado`: procedência da informação (`ENTREVISTA`, `QUESTIONARIO` ou `FONTE_SECUNDARIA`).
- `nomeFonte`: identificação da origem/coleta, devolvida na resposta JSON.

Não é permitido cadastrar mais de um lançamento para a mesma confecção, categoria e mês, mesmo se os valores forem diferentes. A API retorna HTTP 400 quando detecta duplicidade ou valor negativo.

## Migração de procedência dos dados (BACK-04)

O schema original possui `origem` com valores `MANUAL` e `IMPORTACAO`, que indicam como o registro entrou no sistema. A issue também pede a fonte da informação (entrevista, questionário ou fonte secundária). Para não misturar dois conceitos diferentes, esta implementação adiciona os campos `origem_dado` e `nome_fonte` por meio de uma migração separada.

Execute a migração antes de iniciar o perfil MySQL. Registros antigos recebem um valor padrão explícito de legado; substitua esse valor pelo nome da fonte verdadeira quando a equipe puder identificá-la.

## Códigos de erro

O backend retorna erros em JSON:

```json
{
  "timestamp": "2026-10-27T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Categoria já cadastrada",
  "path": "/api/categorias"
}
```

- `400`: dados inválidos, duplicidade ou violação de regra de negócio.
- `404`: recurso não encontrado.
- `500`: erro inesperado, sem expor detalhes internos.

## Testes

```bash
./mvnw clean test
```

Os testes cobrem o health check, CRUD de categorias, CRUD de confecções, duplicidade de categorias, restrição de cidade, criação e duplicidade de vendas, procedência devolvida em JSON e bloqueio de quantidade negativa.

**Status da validação desta entrega:** o `pom.xml` e os arquivos YAML foram verificados estruturalmente. O build Maven e os testes de integração não puderam ser executados neste ambiente de criação, pois Maven não está instalado e o ambiente não conseguiu acessar a internet para baixar as dependências. Execute `./mvnw clean test` ou `mvnw.cmd clean test` no seu computador para confirmar a compilação completa.

## Estrutura do projeto

```text
src/main/java/br/com/trama/
  config/       Configuração de CORS e Spring Security
  controller/   Endpoints REST
  dto/          Contratos JSON e validação de entrada
  error/        Respostas e tratamento de erros
  model/        Entidades JPA
  model/enums/  Enums das regras de domínio
  repository/   Repositórios Spring Data JPA
  service/      Regras de negócio
src/main/resources/
  application.yml
  application-local.yml
  application-dev.yml
  application-sqlite.yml
database/migrations/
  V0_3_2__origem_dado_lancamento.sql
src/test/java/br/com/trama/
  ApiIntegrationTest.java
```

## Limitações conhecidas / próximos passos

- Esta entrega implementa as quatro issues descritas: bootstrap, mapeamento JPA, CRUD de categorias/confecções e API de lançamentos mensais.
- A autenticação/autorização ainda não foi implementada nesta entrega. A configuração atual libera os endpoints para testes locais; não publique com dados reais antes de fechar essa parte.
- A importação CSV completa, indicadores agregados, IA e pesquisa de tendências não pertencem às quatro issues incluídas e podem ser implementadas em issues seguintes.
