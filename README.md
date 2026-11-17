# Issue 02 — Cadastro e validação de artesãos

## Arquivos desta entrega
- `ArtesaoRequest.java`: valida nome, CPF, telefone, WhatsApp e e-mail.
- `Artesao.java`: entidade JPA.
- `ArtesaoRepository.java`: acesso ao banco.
- `ArtesaoController.java`: endpoints de cadastro e listagem.

## Como aplicar
1. Copie os arquivos mantendo os caminhos.
2. A dependência `spring-boot-starter-validation` já existe no backend original.
3. Execute a migration desta issue: `database/migrations/V0_6_0__artesao.sql`.
4. Teste `POST /api/artesaos` com CPF válido e inválido. O corpo usa `nome`, `cpf`, `telefone`, `whatsapp` e `email`.

**Atenção:** esta entrega pressupõe que a migration permita o perfil `ARTESAO`; a alteração da enumeração e da segurança fica na Issue 03.
