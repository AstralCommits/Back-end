## Descrição

Este Pull Request reúne as implementações relacionadas às issues **FRONT-05 (#15)** e **FRONT-06 (#16)**, com o objetivo de ampliar as funcionalidades do marketplace da TRAMA, melhorar a experiência de navegação em dispositivos móveis e disponibilizar uma área de acompanhamento para os artesãos.

As alterações foram desenvolvidas considerando a estrutura visual e a proposta da plataforma, priorizando responsividade, organização dos componentes, facilidade de utilização e separação entre dados demonstrativos e futuras integrações com o backend.

### 1. FRONT-06 (#16) — Responsividade do catálogo

Foi desenvolvida uma interface de catálogo adaptável a diferentes tamanhos de tela, especialmente dispositivos móveis com larguras entre 360px e 480px.

**Principais implementações:**

- Adaptação da disposição dos produtos para telas menores, com organização em uma ou duas colunas conforme o espaço disponível.
- Implementação de menu lateral mobile, acionado por um botão de menu (hambúrguer).
- Inclusão de fundo sobreposto para facilitar o fechamento do menu ao clicar fora dele.
- Fechamento do menu pela tecla `Esc`.
- Bloqueio da rolagem do conteúdo de fundo enquanto o menu estiver aberto.
- Gerenciamento de foco e atributos de acessibilidade, como `aria-expanded`, `aria-controls` e `aria-modal`.
- Ajuste das áreas de interação para facilitar o uso por toque, respeitando a dimensão mínima prevista de 44px.
- Tratamento de espaçamentos e dimensões para reduzir problemas de rolagem horizontal.
- Ajuste do tamanho da fonte dos campos de formulário em dispositivos móveis.

Também foi adicionado o breakpoint personalizado `xs`, correspondente a 360px, para permitir ajustes mais específicos em telas pequenas.

### 2. FRONT-05 (#15) — Painel do artesão

Foi implementada uma tela de acompanhamento dos pedidos recebidos pelo artesão, permitindo visualizar informações financeiras e atualizar o andamento da produção.

**Principais implementações:**

- Cards com resumo financeiro e informações dos pedidos.
- Exibição dos valores previstos para repasse ao artesão.
- Aplicação do percentual mínimo de repasse social de 85%.
- Apresentação do percentual e do valor correspondente ao repasse por pedido.
- Listagem do histórico de pedidos.
- Filtro de pedidos por mês.
- Atualização do status de produção de cada pedido.
- Feedback visual para estados de carregamento, sucesso e erro durante as atualizações.
- Organização da listagem em tabela para telas maiores e em cartões para dispositivos móveis.

O fluxo de status contempla as seguintes etapas:

1. Recebido;
2. Em produção;
3. Pronto;
4. Enviado;
5. Entregue.

### 3. Organização técnica

Para facilitar a manutenção e futuras evoluções, os dados demonstrativos e a lógica da interface foram organizados em arquivos separados.

Foram incluídos:

- Dados simulados de produtos para o catálogo.
- Dados simulados de pedidos para o painel do artesão.
- Serviço responsável por abstrair o acesso aos pedidos, permitindo uma futura integração com a API.
- Funções auxiliares para os cálculos financeiros.
- Componente compartilhado de menu lateral responsivo.
- Rotas específicas para o catálogo e para o painel do artesão.

As novas rotas previstas são:

- `/catalogo`
- `/painel-artesao`

**Importante:** os dados utilizados nesta etapa são demonstrativos. As operações do painel não representam persistência real no banco de dados, e a atualização dos pedidos permanece restrita ao estado da aplicação enquanto não houver integração com o backend.

### 4. Documentação

Também foi incluído o arquivo `README.md` com informações sobre as funcionalidades implementadas, organização dos arquivos e orientações relacionadas à execução e aos testes do projeto.

### 5. Testes e validações

Foi executado o teste automatizado das funções de cálculo financeiro:

```bash
node --test apps/web/tests/orderFinancials.test.mjs
```

**Resultado:** 7 testes executados com sucesso.

As demais validações ainda precisam ser realizadas no ambiente atualizado do projeto:

- [ ] Executar a instalação das dependências.
- [ ] Executar o build da aplicação.
- [ ] Executar a verificação de tipos e o lint, conforme os scripts disponíveis.
- [ ] Verificar visualmente o catálogo nas larguras de 360px, 390px, 420px e 480px.
- [ ] Validar a abertura e o fechamento do menu mobile.
- [ ] Verificar o funcionamento do painel e a atualização dos status.
- [ ] Conferir a integração das novas rotas com a navegação existente.

Não foi possível concluir essas validações nesta etapa, portanto o resultado dos sete testes financeiros não deve ser interpretado como uma validação completa da aplicação.

### 6. Observações para revisão

- Os dados utilizados são fictícios e servem exclusivamente para demonstração.
- As imagens dos produtos ainda precisam ser substituídas ou conectadas às imagens definitivas do projeto.
- A integração real com o backend e a persistência dos pedidos não fazem parte desta implementação.
- A integração dos arquivos com a estrutura atual do repositório deve ser conferida antes da aprovação.

### Issues relacionadas

- Referência: #15 — FRONT-05: Painel do artesão.
- Referência: #16 — FRONT-06: Responsividade do catálogo.

### Resultado esperado

Com essas implementações, espera-se oferecer uma navegação mais adequada em dispositivos móveis e disponibilizar ao artesão uma área organizada para acompanhar pedidos, valores de repasse e etapas de produção, mantendo a estrutura preparada para futuras integrações com os serviços da TRAMA.
