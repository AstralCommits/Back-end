-- Migração necessária para a issue BACK-04.
-- Execute uma única vez no banco MySQL confeccoes já existente.
-- Não recria nem apaga tabelas ou registros.

ALTER TABLE lancamento_mensal
    ADD COLUMN origem_dado VARCHAR(30) NOT NULL DEFAULT 'FONTE_SECUNDARIA',
    ADD COLUMN nome_fonte VARCHAR(200) NOT NULL DEFAULT 'Fonte não informada (registro legado)';

ALTER TABLE lancamento_mensal
    ADD CONSTRAINT chk_lanc_origem_dado
    CHECK (origem_dado IN ('ENTREVISTA', 'QUESTIONARIO', 'FONTE_SECUNDARIA'));

-- Registros anteriores recebem valores explícitos de legado pelo DEFAULT.
-- Revise nome_fonte dos registros existentes para documentar a fonte verdadeira sempre que possível.
