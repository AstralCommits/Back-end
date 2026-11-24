# Publicação do TRAMA na branch dev

1. Extraia o ZIP e abra o terminal na pasta `TRAMA-Entrega`.
2. Configure o repositório remoto da equipe (se ainda não estiver configurado): `git remote -v`.
3. Atualize a branch de destino:

```bash
git fetch origin
git switch dev
git pull --ff-only origin dev
```

Se `dev` não existir localmente: `git switch -c dev --track origin/dev`.

4. Confira e prepare as alterações:

```bash
git status
git add README.md RELATORIO-IMPLEMENTACAO-E-TESTES.md GIT-DEPLOY.md .gitignore .env.example docker-compose.yml Frontend Backend
git diff --cached --stat
git diff --cached
```

5. Faça o commit e envie:

```bash
git commit -m "feat: integra autenticacao e indicadores TRAMA"
git push origin dev
```

Não use `git push --force`. Não adicione `.env`, tokens, senhas, dumps do banco, `node_modules` ou `target`. Revise `git diff --cached` antes do commit. O envio deve ser feito por alguém com permissão no repositório; não compartilhe senha nem token pessoal.
