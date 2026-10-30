# TRAMA | Back-end

API REST do projeto **TRAMA — Tecendo Relações e Analisando o Mercado do Agreste**. O backend é responsável por receber as solicitações do sistema, aplicar as regras de negócio, validar os dados e realizar a comunicação com o banco de dados relacional.

A implementação contempla as issues BACK-01 a BACK-04, utilizando Java 21, Spring Boot 3.3.x, Maven, Spring Web, Spring Data JPA, Bean Validation e Spring Security.

## Objetivo do backend

O backend funciona como intermediário entre a aplicação e o banco de dados SQL. Enquanto o banco armazena e organiza as informações por meio de tabelas, relacionamentos, chaves estrangeiras, procedures e triggers, a API disponibiliza operações para consultar e manipular esses dados.

Nesta etapa, o desenvolvimento está concentrado em:

- Estrutura inicial da aplicação Spring Boot.
- Mapeamento das entidades do modelo relacional.
- Cadastro e gerenciamento de categorias e confecções.
- Cadastro e consulta de lançamentos mensais de vendas.
- Validação das regras de negócio e prevenção de inconsistências.

## Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java 21 | Linguagem de programação |
| Spring Boot 3.3.x | Estrutura principal da aplicação |
| Maven | Gerenciamento de dependências e execução do projeto |
| Spring Web | Criação dos endpoints REST |
| Spring Data JPA | Comunicação entre as entidades Java e o banco relacional |
| Hibernate | Mapeamento objeto-relacional (ORM) |
| Bean Validation | Validação dos dados recebidos pela API |
| Spring Security | Estrutura de segurança da aplicação |
| MySQL 8 | Banco de dados principal |
| H2 | Banco em memória para testes locais |
| SQLite | Alternativa de persistência local |

## Organização da arquitetura

O projeto segue uma arquitetura em camadas, separando as responsabilidades de cada parte da aplicação.

| Camada | Responsabilidade |
|---|---|
| Controller | Recebe as requisições HTTP e retorna as respostas da API |
| Service | Executa as regras de negócio e controla as operações |
| Repository | Realiza o acesso e a consulta aos dados do banco |
| Model | Representa as entidades do modelo relacional |
| DTO | Define os dados recebidos e enviados pela API |
| Config | Reúne as configurações da aplicação |
| Error | Centraliza o tratamento de erros e respostas de exceção |

### Fluxo de funcionamento

Uma requisição passa pelas camadas até chegar ao banco de dados. Após a operação, o resultado retorna ao cliente em formato JSON.

1. O Controller recebe a solicitação HTTP.
2. O DTO recebe os dados e permite a aplicação das validações.
3. O Service verifica as regras de negócio.
4. O Repository realiza a operação de persistência ou consulta.
5. O JPA/Hibernate traduz as operações para SQL.
6. A API retorna o resultado em JSON.

## Entidades e mapeamento relacional

As entidades Java representam as tabelas do banco SQL. O mapeamento é realizado por meio das anotações JPA, como `@Entity`, `@Table`, `@Id`, `@Column` e `@ManyToOne`.

| Entidade Java | Tabela SQL | Finalidade |
|---|---|---|
| Cidade | cidade | Armazena as cidades do polo do Agreste |
| Confeccao | confeccao | Representa as confecções cadastradas |
| Usuario | usuario | Representa os usuários do sistema |
| Categoria | categoria | Organiza os tipos de produtos comercializados |
| LancamentoMensal | lancamento_mensal | Registra os dados mensais de vendas |
| CenarioConcorrencia | cenario_concorrencia | Representa cenários de impacto da concorrência |

### Relacionamentos entre as entidades

Os relacionamentos do modelo relacional são representados nas entidades por meio das associações JPA.

| Relacionamento | Tipo | Funcionamento |
|---|---|---|
| Confecção → Cidade | ManyToOne | Cada confecção está vinculada a uma cidade |
| Lançamento mensal → Confecção | ManyToOne | Cada lançamento pertence a uma confecção |
| Lançamento mensal → Categoria | ManyToOne | Cada lançamento está associado a uma categoria |
| Cenário de concorrência → Usuário | ManyToOne | Cada cenário possui um usuário responsável |
| Cenário de concorrência → Categoria | ManyToOne | Um cenário pode estar associado a uma categoria |

Os repositórios utilizam `JpaRepository`, que disponibiliza operações de persistência e consulta, como salvar, buscar, atualizar e excluir registros, além de permitir a criação de métodos de consulta derivados.

## Regras de negócio e funcionalidades

A implementação das quatro primeiras issues contempla os seguintes requisitos:

### BACK-01 — Estrutura inicial

- Configuração do projeto com Spring Boot 3.3.x e Java 21.
- Gerenciamento de dependências pelo Maven.
- Configuração dos ambientes de execução.
- Inicialização da API na porta 8080.

### BACK-02 — Entidades e repositórios

- Mapeamento das entidades do modelo relacional.
- Definição dos relacionamentos entre as entidades.
- Criação dos repositórios com Spring Data JPA.

### BACK-03 — Categorias e confecções

**RF01 — Categorias**

- Nome obrigatório.
- Limite de 50 caracteres.
- Não permite categorias duplicadas.
- Retorna HTTP 400 com a mensagem `Categoria já cadastrada` em caso de duplicidade.

**RF02 — Confecções**

- Nome e cidade obrigatórios.
- Cidades permitidas: Toritama, Caruaru e Santa Cruz do Capibaribe.
- Cadastro, consulta, alteração e exclusão de confecções.

### BACK-04 — Lançamentos mensais

**RF03 — Registro de vendas**

- Categoria e confecção obrigatórias.
- Mês e ano no formato `AAAA-MM`.
- Quantidade inteira maior ou igual a zero.
- Faturamento decimal maior ou igual a zero.
- Bloqueio de registros duplicados para a mesma confecção, categoria e mês.

**RF04 — Procedência dos dados**

A API diferencia duas informações:

| Campo | Finalidade | Valores |
|---|---|---|
| `origem` | Indica como o registro entrou no sistema | MANUAL, IMPORTACAO |
| `origemDado` | Indica de onde veio a informação de venda | ENTREVISTA, QUESTIONARIO, FONTE_SECUNDARIA |
| `nomeFonte` | Identifica a fonte ou coleta da informação | Nome da fonte correspondente |

Essa separação permite identificar tanto o método de inclusão do lançamento quanto a procedência da informação utilizada.

## Endpoints da API

Os endpoints são responsáveis por disponibilizar as operações do backend por meio do protocolo HTTP.

### Saúde da aplicação

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/health` | Verifica se a aplicação está funcionando |

### Cidades

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/cidades` | Consulta as cidades homologadas |

### Categorias

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/categorias` | Lista as categorias |
| GET | `/api/categorias/{id}` | Consulta uma categoria |
| POST | `/api/categorias` | Cadastra uma categoria |
| PUT | `/api/categorias/{id}` | Atualiza uma categoria |
| DELETE | `/api/categorias/{id}` | Exclui uma categoria |

### Confecções

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/confeccoes` | Lista as confecções |
| GET | `/api/confeccoes/{id}` | Consulta uma confecção |
| POST | `/api/confeccoes` | Cadastra uma confecção |
| PUT | `/api/confeccoes/{id}` | Atualiza uma confecção |
| DELETE | `/api/confeccoes/{id}` | Exclui uma confecção |

### Lançamentos mensais

| Método | Endpoint | Finalidade |
|---|---|---|
| POST | `/api/vendas` | Cadastra um lançamento mensal |
| GET | `/api/vendas` | Lista lançamentos com filtros |
| GET | `/api/vendas/{id}` | Consulta um lançamento |
| PUT | `/api/vendas/{id}` | Atualiza um lançamento |
| DELETE | `/api/vendas/{id}` | Realiza o cancelamento lógico do lançamento |

A consulta de vendas permite filtrar os registros por categoria, confecção e mês de referência:

`GET /api/vendas?categoria=1&confeccao=1&mesAno=2026-10`

O cancelamento lógico altera o status do lançamento para `CANCELADO`, preservando o registro no banco de dados.

## Integração com o banco de dados SQL

O backend utiliza o modelo relacional desenvolvido pela equipe de Banco de Dados. A estrutura das tabelas, as chaves estrangeiras, as procedures, os triggers e as transações são definidos nos scripts SQL do projeto.

A comunicação com o banco acontece por meio do Spring Data JPA e do Hibernate.

### Ambientes de persistência

A aplicação possui três configurações:

| Perfil | Banco | Finalidade |
|---|---|---|
| `local` | H2 | Execução e testes sem depender do MySQL |
| `dev` | MySQL 8 | Integração com o banco relacional da equipe |
| `sqlite` | SQLite | Persistência em arquivo local |

No perfil `local`, o banco H2 funciona em memória. As informações são reiniciadas quando a aplicação é encerrada. São inseridas três cidades homologadas e cinco categorias iniciais para facilitar os testes.

No perfil `dev`, a aplicação utiliza o banco `confeccoes`, já estruturado pelos scripts SQL da equipe. A configuração `ddl-auto: validate` verifica se a estrutura existente corresponde ao mapeamento JPA, sem recriar automaticamente as tabelas.

### Migração da procedência dos dados

O banco original já possui o campo `origem`, utilizado para indicar se o lançamento foi inserido manualmente ou por importação.

Como o requisito RF04 também exige identificar a fonte da informação, foram acrescentados dois campos:

- `origem_dado`: identifica se a informação veio de entrevista, questionário ou fonte secundária.
- `nome_fonte`: identifica a fonte correspondente.

Essa alteração é aplicada pela migração:

`database/migrations/V0_3_2__origem_dado_lancamento.sql`

A migração complementa a tabela `lancamento_mensal`, sem recriar o banco de dados.

## Tratamento de erros

A API utiliza respostas JSON padronizadas para informar o resultado das operações.

| Código HTTP | Significado | Exemplo |
|---|---|---|
| 200 | Operação realizada com sucesso | Consulta de registros |
| 201 | Recurso criado | Cadastro de categoria |
| 400 | Dados inválidos ou regra de negócio violada | Categoria duplicada |
| 404 | Recurso não encontrado | ID inexistente |
| 500 | Erro inesperado | Falha interna da aplicação |

Exemplo de resposta:

```json
{
  "timestamp": "2026-10-27T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Categoria já cadastrada",
  "path": "/api/categorias"
}
```

## Testes

A aplicação possui testes de integração utilizando MockMvc, ferramenta do Spring utilizada para simular requisições HTTP e verificar as respostas dos endpoints.

Os testes locais são executados pelo Maven:

```bash
./mvnw clean test
```

No Windows:

```bat
mvnw.cmd clean test
```

Na execução local registrada, foram realizados 7 testes automatizados, sem falhas ou erros.

Também foram realizados testes manuais com o banco H2 para verificar:

- Consulta das categorias e confecções.
- Cadastro e consulta de confecções.
- Cadastro e consulta de lançamentos mensais.
- Consulta de vendas utilizando filtros.
- Bloqueio de lançamentos duplicados, com retorno HTTP 400.
- Retorno da procedência dos dados na resposta JSON.

A integração com o MySQL da equipe ainda depende da validação da conexão e do schema existente.

## Segurança

O projeto inclui a dependência Spring Security. Nesta etapa, a configuração dos endpoints permanece aberta para facilitar a execução e a integração das funcionalidades implementadas.

A autenticação, a autorização e o isolamento dos dados por confecção ainda precisam ser implementados antes da utilização em produção com dados reais.

## Estrutura de diretórios

```text
TRAMA-Backend/
│
├── database/
│   ├── migrations/
│   │   └── V0_3_2__origem_dado_lancamento.sql
│   └── schema_reference.sql
│
├── src/
│   ├── main/
│   │   ├── java/br/com/trama/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── error/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-dev.yml
│   │       └── application-sqlite.yml
│   │
│   └── test/
│       └── java/br/com/trama/
│           └── ApiIntegrationTest.java
│
├── pom.xml
└── mvnw
```

## Como executar

O backend pode ser executado utilizando o Maven Wrapper, sem exigir uma instalação global do Maven, desde que o ambiente tenha Java 21.

### Execução local

Windows:

```bat
mvnw.cmd clean test
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

A aplicação será iniciada em:

`http://localhost:8080`

Verificação de funcionamento:

```bash
curl http://localhost:8080/api/health
```

Resposta esperada:

```json
{
  "status": "UP",
  "application": "TRAMA Backend"
}
```

### Conexão com MySQL

Para utilizar o banco relacional da equipe, é necessário configurar as variáveis de ambiente:

- `DB_URL`: endereço de conexão com o banco `confeccoes`.
- `DB_USERNAME`: usuário autorizado do MySQL.
- `DB_PASSWORD`: senha correspondente.

Após a configuração, a aplicação deve ser iniciada com o perfil `dev`.

A migração `V0_3_2__origem_dado_lancamento.sql` deve ser aplicada uma única vez ao banco existente antes da utilização das funcionalidades de procedência dos dados.

**Atenção:** o script `banco_completo.sql`, pertencente ao projeto de Banco de Dados, recria o banco do zero. Ele não deve ser executado em uma base que contenha informações a preservar.

## Escopo e próximos desenvolvimentos

A entrega atual contempla as quatro primeiras issues do backend:

| Issue | Implementação |
|---|---|
| BACK-01 | Configuração inicial do Spring Boot |
| BACK-02 | Entidades JPA e repositórios |
| BACK-03 | CRUD de categorias e confecções |
| BACK-04 | Cadastro e consulta de lançamentos mensais |

As funcionalidades de importação completa de planilhas, geração de relatórios, indicadores de mercado, análise de tendências e integração com IA fazem parte de etapas posteriores do desenvolvimento do TRAMA.
