# Banco de dados e migrações

O backend foi mapeado a partir do schema MySQL `confeccoes` fornecido pela equipe.

- `schema_reference.sql`: referência do DDL base para um banco vazio. Não o execute sobre uma base com tabelas/dados já existentes.
- `migrations/V0_3_2__origem_dado_lancamento.sql`: migração aditiva necessária para `origem_dado` e `nome_fonte`, sem apagar dados.

O script SQL original da equipe começa com `DROP DATABASE IF EXISTS confeccoes`. Não o execute em um ambiente que contenha dados que devam ser preservados.
