-- Alinhamento com a issue BD #17.
-- Execute somente em um banco que AINDA usa o perfil CONFECCAO e não possui
-- a constraint chk_cenario_impacto. Não execute se o PR de banco já aplicou estas mudanças.

ALTER TABLE usuario DROP CHECK chk_usuario_perfil;
ALTER TABLE usuario DROP CHECK chk_usuario_perfil_confeccao;

UPDATE usuario
SET perfil = 'EMPRESARIO'
WHERE perfil = 'CONFECCAO';

ALTER TABLE usuario
    ADD CONSTRAINT chk_usuario_perfil
    CHECK (perfil IN ('ADMIN', 'EMPRESARIO'));

ALTER TABLE usuario
    ADD CONSTRAINT chk_usuario_perfil_confeccao
    CHECK (
        (perfil = 'ADMIN' AND id_confeccao IS NULL)
        OR (perfil = 'EMPRESARIO' AND id_confeccao IS NOT NULL)
    );

ALTER TABLE cenario_concorrencia
    ADD CONSTRAINT chk_cenario_impacto
    CHECK (percentual_impacto BETWEEN 0 AND 100);
