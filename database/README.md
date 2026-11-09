# Banco de dados e migrações do backend

O backend utiliza o schema MySQL `confeccoes` mantido pela equipe de Banco de Dados. O schema canônico é o da branch `develop` do repositório SQL; os arquivos desta pasta são referências para o mapeamento JPA e migrações complementares da API.

- `schema_reference.sql`: referência das tabelas principais já alinhada às alterações do PR #6 (perfil `EMPRESARIO`, `fonte_dado`, `chk_cenario_impacto`, tabela `historico_lancamento`) e inclui a extensão proposta `nome_fonte` para atender ao RF04. Não executar sobre uma base existente.
- `migrations/V0_5_0__nome_fonte_lancamento.sql`: acrescenta apenas `nome_fonte` à tabela `lancamento_mensal`, pois a branch atual já possui `fonte_dado`. A equipe de Banco de Dados deve aprovar e integrar essa extensão antes da validação MySQL.
- `migrations/V0_4_0__perfil_empresario_e_impacto.sql`: compatibilidade para bancos antigos que ainda usam perfil `CONFECCAO`; não executar em bancos que já receberam o PR #6.

O PR #6 do repositório Banco-de-Dados foi integrado à `develop` em 3 de novembro de 2026. O ZIP do banco recebido nesta revisão ainda estava desatualizado, portanto a branch remota `develop` deve ser considerada a fonte de verdade.

O script SQL principal da equipe pode recriar o banco do zero. Não o execute em ambiente com dados que devam ser preservados.
