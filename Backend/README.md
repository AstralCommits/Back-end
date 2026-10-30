# TRAMA | Backend

API REST do projeto **TRAMA — Tecendo Relações e Analisando o Mercado do Agreste**.

O backend é responsável por disponibilizar os serviços que permitem cadastrar, consultar e gerenciar informações relacionadas às confecções, categorias e lançamentos mensais de vendas do Polo de Confecções do Agreste Pernambucano.

A aplicação foi desenvolvida com Java 21, Spring Boot 3.3.x e Maven, utilizando Spring Web, Spring Data JPA, Bean Validation e Spring Security.

## 1. Contexto e objetivo

O backend foi desenvolvido para atender às primeiras quatro demandas técnicas do projeto, identificadas como BACK-01 a BACK-04.

Seu objetivo é disponibilizar uma API REST que faça a comunicação entre a aplicação e o banco de dados, centralizando as regras de negócio e garantindo a validação das informações cadastradas.

As funcionalidades implementadas neste escopo são:

- Configuração inicial da aplicação Spring Boot.
- Mapeamento das principais entidades do modelo relacional.
- Cadastro, consulta, atualização e exclusão de categorias e confecções.
- Cadastro e consulta de lançamentos mensais de vendas.
- Validação de dados e bloqueio de registros duplicados.
- Registro da procedência das informações de vendas.

## 2. Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java 21 LTS | Linguagem de programação |
| Spring Boot 3.3.x | Estrutura principal da aplicação |
| Maven | Gerenciamento de dependências e build |
| Spring Web | Criação dos endpoints REST |
| Spring Data JPA | Persistência e comunicação com o banco |
| Hibernate | Mapeamento objeto-relacional |
| Bean Validation | Validação dos dados recebidos |
| Spring Security | Configuração de segurança da aplicação |
| MySQL 8 | Banco de dados principal |
| SQLite | Suporte a banco em arquivo |
| H2 | Banco em memória para testes locais |
| JUnit e MockMvc | Testes automatizados da API |

## 3. Arquitetura do backend

A aplicação utiliza uma arquitetura em camadas, separando as responsabilidades de cada parte do sistema.

| Camada | Responsabilidade |
|---|---|
| Controller | Recebe as requisições HTTP e disponibiliza os endpoints da API. |
| Service | Executa as regras de negócio e controla as operações. |
| Repository | Realiza o acesso e a persistência dos dados no banco. |
| Model | Representa as entidades do banco de dados por meio de classes Java. |
| DTO | Define os dados recebidos e enviados pela API, incluindo validações. |
| Config | Centraliza as configurações da aplicação e da segurança. |
| Error | Padroniza o tratamento e as respostas de erros. |

O fluxo de uma requisição acontece da seguinte forma:

1. O cliente envia uma requisição HTTP para um endpoint.
2. O Controller recebe os dados e encaminha a operação.
3. O Service aplica as regras de negócio e validações necessárias.
4. O Repository realiza a consulta ou alteração no banco de dados.
5. A aplicação retorna uma resposta JSON ao cliente.

## 4. Mapeamento das entidades JPA

As entidades representam as estruturas do modelo relacional do banco de dados dentro da aplicação Java. O mapeamento é realizado utilizando anotações JPA, como `@Entity`, `@Table`, `@Id`, `@ManyToOne` e `@JoinColumn`.

### Entidades implementadas

| Entidade | Principais atributos | Relacionamentos |
|---|---|---|
| Cidade | id, nome, uf | Referenciada por Confecção. |
| Confecção | id, nome, cnpj, porte, tipo | ManyToOne com Cidade. |
| Usuário | id, nome, email, senha_hash, perfil | Relacionado à Confecção quando aplicável e aos registros de domínio correspondentes. |
| Categoria | id, nome | Referenciada por Lançamento Mensal e Cenário de Concorrência. |
| Lançamento Mensal | id, mesReferencia, quantidade, faturamento, origem | ManyToOne com Confecção e Categoria. |
| Cenário de Concorrência | id, nome, percentualImpacto | ManyToOne com Categoria e Usuário. |

### Relacionamentos principais

- **Cidade e Confecção:** uma cidade pode estar relacionada a várias confecções, mas cada confecção pertence a uma cidade.
- **Confecção e Lançamento Mensal:** uma confecção pode possuir vários lançamentos, mas cada lançamento está associado a uma confecção.
- **Categoria e Lançamento Mensal:** uma categoria pode classificar vários lançamentos, mas cada lançamento possui uma categoria.
- **Categoria e Cenário de Concorrência:** uma categoria pode estar associada a diferentes cenários.
- **Usuário e Cenário de Concorrência:** um usuário pode criar diferentes cenários de concorrência.

Os relacionamentos são representados nas entidades Java por meio das anotações JPA e das respectivas chaves estrangeiras do banco.

### Repositórios

Foram criados repositórios utilizando `JpaRepository`, responsável por disponibilizar operações de persistência, consulta, atualização e exclusão.

Os repositórios centralizam o acesso às entidades e permitem utilizar métodos de consulta derivados, evitando a necessidade de escrever SQL manual para as operações básicas.

## 5. Regras de negócio implementadas

As regras de negócio são aplicadas na camada Service e complementadas pelas validações dos DTOs e pelas restrições do banco de dados.

| Regra | Funcionamento |
|---|---|
| Categoria obrigatória | O nome da categoria não pode ser vazio. |
| Categoria única | Não é permitido cadastrar categorias duplicadas. |
| Limite de caracteres | O nome da categoria aceita até 50 caracteres. |
| Cidade homologada | Confecções devem estar associadas a Toritama, Caruaru ou Santa Cruz do Capibaribe. |
| Campos obrigatórios | Nome e cidade são obrigatórios no cadastro de uma confecção. |
| Quantidade válida | A quantidade de vendas deve ser um número inteiro maior ou igual a zero. |
| Faturamento válido | O valor informado deve ser decimal maior ou igual a zero. |
| Mês de referência | O lançamento deve utilizar o formato `AAAA-MM`. |
| Prevenção de duplicidade | Não pode existir mais de um lançamento para a mesma confecção, categoria e mês. |
| Procedência do dado | Cada lançamento deve identificar a origem da informação e o nome da fonte. |

### Procedência dos dados

A aplicação diferencia duas informações:

- `origem`: identifica como o registro foi inserido no sistema, podendo ser `MANUAL` ou `IMPORTACAO`.
- `origemDado`: identifica de onde veio a informação, podendo ser `ENTREVISTA`, `QUESTIONARIO` ou `FONTE_SECUNDARIA`.

O campo `nomeFonte` registra a identificação da fonte utilizada.

Essa separação evita confundir o método de inclusão do registro com a procedência da informação.

## 6. Endpoints REST

Os endpoints seguem o padrão REST, utilizando os métodos HTTP para realizar operações sobre os recursos da aplicação. As respostas são disponibilizadas no formato JSON.

### Saúde da aplicação

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/health` | Verifica se a API está funcionando. |

### Cidades

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/cidades` | Lista as cidades homologadas para cadastro de confecções. |

### Categorias

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/categorias` | Lista as categorias. |
| GET | `/api/categorias/{id}` | Consulta uma categoria específica. |
| POST | `/api/categorias` | Cadastra uma categoria. |
| PUT | `/api/categorias/{id}` | Atualiza uma categoria. |
| DELETE | `/api/categorias/{id}` | Exclui uma categoria. |

**Validações:** o nome é obrigatório, possui limite de 50 caracteres e não pode ser duplicado. Uma tentativa de cadastro duplicado retorna HTTP 400 com a mensagem `Categoria já cadastrada`.

### Confecções

| Método | Endpoint | Finalidade |
|---|---|---|
| GET | `/api/confeccoes` | Lista as confecções cadastradas. |
| GET | `/api/confeccoes/{id}` | Consulta uma confecção específica. |
| POST | `/api/confeccoes` | Cadastra uma confecção. |
| PUT | `/api/confeccoes/{id}` | Atualiza uma confecção. |
| DELETE | `/api/confeccoes/{id}` | Exclui uma confecção. |

**Validações:** nome e cidade são obrigatórios. São permitidas apenas as cidades de Toritama, Caruaru e Santa Cruz do Capibaribe. O porte aceita `MICRO`, `PEQUENA` ou `MEDIA`, enquanto o tipo aceita `CONFECCAO` ou `FACCAO`.

### Lançamentos mensais de vendas

| Método | Endpoint | Finalidade |
|---|---|---|
| POST | `/api/vendas` | Cadastra um lançamento mensal. |
| GET | `/api/vendas` | Lista lançamentos com filtros. |
| GET | `/api/vendas/{id}` | Consulta um lançamento específico. |
| PUT | `/api/vendas/{id}` | Atualiza um lançamento. |
| DELETE | `/api/vendas/{id}` | Cancela logicamente um lançamento. |

O endpoint de listagem permite utilizar os seguintes parâmetros:

- `categoria`: identificador da categoria.
- `confeccao`: identificador da confecção.
- `mesAno`: mês e ano de referência no formato `AAAA-MM`.

Exemplo:

`GET /api/vendas?categoria=1&confeccao=1&mesAno=2026-10`

### Estrutura de um lançamento

Exemplo de requisição para cadastro:

```json
{
  "idConfeccao": 1,
  "idCategoria": 1,
  "mesAno": "2026-10",
  "quantidade": 120,
  "faturamento": 3600.00,
  "origem": "MANUAL",
  "origemDado": "ENTREVISTA",
  "nomeFonte": "Entrevista de teste"
}
```

O backend valida os campos recebidos, verifica a existência da confecção e da categoria e impede a criação de outro lançamento para a mesma combinação de confecção, categoria e mês.

O mês informado é armazenado no banco como o primeiro dia do mês de referência.

### Cancelamento de lançamentos

O endpoint DELETE de vendas realiza o cancelamento lógico do registro, alterando seu status para `CANCELADO`.

O lançamento permanece armazenado no banco, permitindo preservar seu histórico. Registros cancelados não devem ser considerados nos indicadores e relatórios.

## 7. Integração com o banco de dados SQL

O backend foi estruturado para trabalhar com o modelo relacional desenvolvido pela equipe de Banco de Dados.

O banco principal do projeto utiliza MySQL 8 e possui estruturas
