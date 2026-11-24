# Changelog — Pacote de Ester

- BACK-01 a BACK-04: estrutura inicial, entidades, CRUD e lançamentos mensais (já existentes no commit histórico `3f56d09`).
- BACK-08 (#12): tratamento global Problem Details / RFC 7807.
- BACK-09 (#13): importação CSV transacional.
- BD #17: perfil `EMPRESARIO`, isolamento por confecção e impacto 0–100.
- SEC (OWASP XSS/Injection): recusa de HTML/scripts nos textos livres com `@Pattern`/`@Size`/`@NotBlank`, sanitização de caracteres de controle no JSON, validação da fonte no CSV e headers de segurança (`X-Content-Type-Options`, `X-Frame-Options`, CSP, Referrer-Policy, HSTS).
- BACK-05 (#26): `POST /api/avaliacoes` (pedidoId, nota 1–5, comentário até 500). Só pedidos `ENTREGUE` e uma avaliação por pedido; autor = quem fez o pedido ou ADMIN. Respostas: 201, 400, 403, 404, 409. Entidades `Pedido` e `AvaliacaoConfeccao` (tabelas `pedido` e `avaliacoes_artesao`; nota 1–5 e unicidade por pedido). `GET /api/confeccoes/{id}/reputacao` lê a view `vw_reputacao_artesao` (nota média e total).
- Ajuste de integração: `fonte_dado` do schema atual e migração complementar para `nome_fonte` (RF04).

OpenAPI, testes ampliados, JaCoCo/SonarQube e GitHub Actions estão no pacote de sobreposição de Gabriela.
