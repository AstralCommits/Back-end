# TRAMA — Relatório de implementação e validação

Data da preparação: 09/11/2026.

## Implementado nesta versão

| Área | Alterações | Situação do código |
|---|---|---|
| Autenticação | Cadastro público de confecção/usuário, login, consulta de sessão, perfil e logout | Implementado |
| Segurança de sessão | BCrypt, JWT HS256 em cookie HttpOnly, CSRF double-submit e CORS com origens explícitas | Implementado; exige configuração correta por ambiente |
| Administração | Provisionamento de ADMIN por variáveis do servidor; endpoint e formulário para cadastro de funcionários vinculados a confecção | Implementado |
| Autorização | Rotas protegidas; operações de confecção/categoria administrativas; vendas e indicadores limitados à confecção autenticada | Implementado no código; testes precisam ser executados |
| Indicadores | Front consulta `/api/indicadores`, série mensal e agregados; modo demonstração identifica dados fictícios | Implementado |
| MySQL | Perfil `dev` para MySQL 8, schema de referência e migração aditiva documentados; `ddl-auto=validate` | Preparado; precisa aplicar schema/migração em banco de destino |
| Documentação | README raiz, README do front/back, `.env.example`, Docker Compose e comandos Git | Criados |

## Testes e verificações nesta sessão

| Verificação | Resultado | Observação |
|---|---|---|
| Inspeção das estruturas e contratos | Realizada | Front-end, back-end e pacote de integração revisados |
| Conferência dos endpoints de indicadores | Realizada por leitura do código | Contrato `inicio`, `fim`, `indicadores` e `faturamentoMensal` conferido |
| Node.js | Não conforme | Ambiente tem Node 22.16.0; projeto exige >=22.22.0 |
| `npm ci` | Não concluído | Execução excedeu 120 segundos; houve avisos de engine e o processo expirou |
| Typecheck React | Não executado | Dependências não foram instaladas por completo |
| Lint React | Não executado | Dependências não foram instaladas por completo |
| Build React | Não executado | Dependências não foram instaladas por completo |
| Maven | Não disponível | `mvn` não está instalado; o wrapper pode baixar Maven na máquina da equipe, mas isso depende de rede |
| Testes Spring Boot | Não executados | Foram atualizados/adicionados testes de integração, mas não foi possível executá-los nesta sessão |
| MySQL real | Não executado | Não havia instância MySQL disponível |
| Teste de navegador / console | Não executado | Exige iniciar os dois servidores e navegador com ambiente configurado |
| Integridade dos ZIPs | Aprovada | Os três ZIPs foram reabertos com sucesso e passaram em `testzip()` |

**Não considere builds ou testes de integração aprovados.** O código foi alterado, mas a execução final precisa ser feita com Node 22.22+, Java 21, Maven e MySQL disponíveis.

## Limitações funcionais conhecidas

1. Vendas diárias, estoque, produção, financeiro, clientes/fornecedores e demais registros operacionais do front-end continuam armazenados no navegador. A autenticação e os indicadores usam o back-end, mas esses módulos ainda não têm persistência sincronizada na API.
2. O back-end registra lançamentos mensais agregados por confecção/categoria/mês; não há contrato para converter automaticamente cada venda diária local em lançamento mensal. A conversão automática poderia duplicar ou distorcer valores.
3. Os usuários cadastrados como funcionários recebem o perfil `CONFECCAO`, com o mesmo escopo da confecção. Não há ainda matriz de permissões por módulo ou cargo.
4. Logout remove os cookies no navegador. Como o JWT é stateless, um token que tenha sido copiado continua válido até expirar (padrão 12 horas); não existe blacklist/revogação imediata.
5. Não foram implementados fluxo de recuperação de senha, verificação de e-mail ou bloqueio/rate limit de tentativas de login.
6. O cadastro valida formato e tamanho de CNPJ, mas não executa validação matemática dos dígitos verificadores.
7. O perfil MySQL usa `ddl-auto=validate`; a equipe deve criar o schema e aplicar a migração documentada antes de iniciar o back-end.

## Configuração necessária antes de executar

- Node.js 22.22 ou superior.
- Java 21 e Maven 3.9+ (ou executar `mvnw.cmd` / `./mvnw` com acesso à rede para baixar Maven).
- MySQL 8 e schema inicial/migração aplicados.
- `TRAMA_JWT_SECRET` gerado com `openssl rand -base64 32`.
- `TRAMA_ADMIN_EMAIL` e `TRAMA_ADMIN_PASSWORD` definidos no ambiente do servidor. Senha de pelo menos 12 caracteres.
- `TRAMA_CORS_ALLOWED_ORIGINS=http://localhost:3000` para desenvolvimento local.
- `VITE_API_URL=http://localhost:8080` em `Frontend/apps/web/.env`.

## Próximo passo recomendado

Executar os comandos da seção “Testes” do README raiz em uma máquina com as versões necessárias. Corrigir qualquer falha real e anexar o log da execução. Só depois aprovar merge/push para `dev`.
