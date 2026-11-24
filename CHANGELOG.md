# Changelog — Pacote de Ester

- BACK-01 a BACK-04: estrutura inicial, entidades, CRUD e lançamentos mensais (já existentes no commit histórico `3f56d09`).
- BACK-08 (#12): tratamento global Problem Details / RFC 7807.
- BACK-09 (#13): importação CSV transacional.
- BD #17: perfil `EMPRESARIO`, isolamento por confecção e impacto 0–100.
- SEC (OWASP XSS/Injection): recusa de HTML/scripts nos textos livres com `@Pattern`/`@Size`/`@NotBlank`, sanitização de caracteres de controle no JSON, validação da fonte no CSV e headers de segurança (`X-Content-Type-Options`, `X-Frame-Options`, CSP, Referrer-Policy, HSTS).
- TEST-02 (#27): `ComissaoService` (90% repasse à confecção, 10% taxa da plataforma, HALF_EVEN em 2 casas, repasse = total − taxa) e `ComissaoServiceTest` (JUnit 5). O JaCoCo (`mvn verify`) exige 100% de linhas e ramos nessa classe.
- Ajuste de integração: `fonte_dado` do schema atual e migração complementar para `nome_fonte` (RF04).

OpenAPI, testes ampliados, JaCoCo/SonarQube e GitHub Actions estão no pacote de sobreposição de Gabriela.
