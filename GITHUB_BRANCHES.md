# Fluxo de branches do TRAMA Backend

O repositório é único e o desenvolvimento é feito em sequência. As branches têm nomes relacionados às funcionalidades, não às pessoas. Ester e Gabriela trabalham no mesmo fluxo: a segunda etapa começa somente depois que a primeira for integrada à `develop`.

## Estrutura

- `main`: versão estável do projeto. Recebe somente versões revisadas e aprovadas.
- `develop`: branch de integração. É a base de todas as branches de funcionalidade.
- `feature/backend-regras-importacao`: primeira etapa de desenvolvimento, com as issues #12, #13 e #17.
- `feature/backend-documentacao-qualidade`: segunda etapa, com as issues #11, #14, #15 e #16.

Os nomes das branches descrevem o conteúdo técnico. Não devem ser criadas branches chamadas `feature/ester` ou `feature/gabriela`.

## Histórico existente

As issues #1 a #4 já possuem implementação no commit histórico `3f56d09`, que foi enviado diretamente à `main`. Ao criar `develop` a partir da `main`, esse histórico e esses arquivos serão preservados. Não é necessário recriar commits artificiais para essas issues.

A `main` não deve ser apagada nem esvaziada com `force push`. O objetivo é parar de desenvolver diretamente nela e passar a usar `develop` como branch padrão de trabalho. Depois de criar e publicar `develop`, o grupo pode definir `develop` como branch padrão em **Settings → General → Default branch**, conforme a organização acordada pela equipe.

## Sequência de trabalho

### Preparação única: criar `develop`

1. Abra o repositório `Back-end` no GitHub Desktop.
2. Selecione a branch `main`.
3. Use **Branch → New Branch** e crie `develop` com base na `main`.
4. Publique `develop` no GitHub.
5. Não apague nem altere o histórico de `main`.

### Etapa 1: primeira branch de funcionalidade

Branch: `feature/backend-regras-importacao`

Responsabilidade prevista: Ester Reis.

Issues desta etapa:

- #12 — BACK-08: tratamento global de exceções com Problem Details.
- #13 — BACK-09: importação de lançamentos por CSV.
- #17 — adequação aos perfis `ADMIN` e `EMPRESARIO` e limite de impacto do cenário entre 0 e 100.

Procedimento:

1. Crie `feature/backend-regras-importacao` a partir da `develop` atualizada.
2. Extraia o pacote da Etapa 1 e copie seu conteúdo para a raiz do repositório local, substituindo os arquivos correspondentes. Preserve a pasta oculta `.git`.
3. Revise as alterações e faça os commits reais referentes às entregas desta etapa.
4. Envie a branch e abra um Pull Request com destino a `develop`.
5. Aguarde revisão e integração do Pull Request antes de iniciar a etapa seguinte.

### Etapa 2: continuação do mesmo projeto

Branch: `feature/backend-documentacao-qualidade`

Responsabilidade prevista: Gabriela Olegário.

Issues desta etapa:

- #11 — DOC-01: documentação interativa OpenAPI / Swagger UI.
- #14 — TEST-01: ampliação dos testes JUnit 5 e MockMvc.
- #15 — DEVOPS-02: JaCoCo e integração com SonarQube.
- #16 — DEVOPS-01: workflow de CI com GitHub Actions.

Procedimento:

1. Depois que o Pull Request da Etapa 1 estiver integrado, atualize a branch `develop` local.
2. Crie `feature/backend-documentacao-qualidade` a partir dessa `develop` atualizada.
3. Aplique o pacote de sobreposição da Etapa 2 sobre a raiz do mesmo repositório. Ele complementa o código da etapa anterior; não é um backend independente.
4. Revise as configurações, execute os testes e valide o workflow. Configure as credenciais institucionais do SonarQube em Secrets/Variables do GitHub, sem colocá-las no código.
5. Faça os commits reais das entregas, publique a branch e abra um Pull Request para `develop`.

## Encerramento e lançamento

Quando as duas etapas estiverem integradas e revisadas, `develop` conterá o backend integrado. A passagem para `main` deve ocorrer somente por Pull Request de `develop` para `main`, depois da validação do grupo e da orientação do professor.

## Cuidados ao copiar os ZIPs

- Copie o conteúdo do pacote para a raiz do clone do repositório, onde fica a pasta `.git`.
- Nunca substitua nem apague a pasta `.git`; ela contém o vínculo remoto e o histórico local.
- O `pom.xml`, `src/` e `.github/` devem ficar diretamente na raiz do repositório, não dentro de uma segunda pasta `Backend/`.
- A Etapa 2 é uma sobreposição sobre a Etapa 1 já integrada. Não aplique o overlay sobre a versão antiga da `main`.
- Não use `Closes #...` nos Pull Requests até que os critérios de aceitação de cada issue tenham sido realmente verificados.
