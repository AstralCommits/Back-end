# Issue 04 — Produtos e cotação corporativa

## Arquivos desta entrega
Cria cadastro/listagem de produtos, criação de cotação, itens da cotação, status `EM_ANALISE_ARTESAO` e notificações internas persistidas para os artesãos associados aos produtos.

## Como aplicar
1. Aplique primeiro a Issue 02 (entidade e tabela de artesão).
2. Copie os arquivos Java mantendo os caminhos.
3. Execute a migration `database/migrations/V0_7_0__produtos_cotacoes.sql` depois da migration da Issue 02.
4. Cadastre artesão e produto; depois faça `POST /api/cotacoes` enviando a lista de IDs de produtos e `prazoEntrega`.
5. A rota de cotação exige token de `EMPRESARIO` (Issue 03).

O endpoint responde HTTP 201. As notificações são gravadas no banco; não há envio externo por WhatsApp ou e-mail.
