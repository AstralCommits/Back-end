# Issue 03 — JWT e autorização por perfil

## Arquivos desta entrega
- `JwtService.java`: inclui a claim `role` no token.
- `JwtAuthenticationFilter.java`: valida a role do token contra as authorities do usuário.
- `PerfilUsuario.java`: adiciona `ARTESAO`.
- `SecurityConfig.java`: restringe `/api/cotacoes/**` a `EMPRESARIO`.

## Como aplicar
1. Copie os arquivos mantendo os caminhos, substituindo as versões existentes.
2. Garanta que `UsuarioDetailsService`/`UsuarioPrincipal` carreguem a autoridade `ROLE_ARTESAO` para usuários desse perfil. O ZIP não altera essas classes porque a implementação atual deve ser conferida com o cadastro de usuários.
3. Faça login novamente após atualizar: tokens antigos não possuem a claim `role`.
4. Teste acesso à cotação com `EMPRESARIO` (permitido) e `ARTESAO` (HTTP 403).

**Dependência:** Issue 04 cria as rotas `/api/cotacoes/**` protegidas por esta configuração.
