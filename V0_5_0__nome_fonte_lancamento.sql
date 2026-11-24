-- Extensão complementar do backend para atender ao RF04: armazenar também
-- o nome específico da fonte/coleta, além do tipo já armazenado em fonte_dado.
-- A branch Banco-de-Dados/develop já possui fonte_dado; este ALTER adiciona somente nome_fonte.
-- Execute uma única vez em um banco que ainda não possua a coluna nome_fonte.

ALTER TABLE lancamento_mensal
    ADD COLUMN nome_fonte VARCHAR(200) NOT NULL
    DEFAULT 'Fonte não informada (registro legado)';
