-- Issue 02: tabela de artesãos e liberação do perfil ARTESAO no MySQL.
ALTER TABLE usuario DROP CHECK chk_usuario_perfil;
ALTER TABLE usuario ADD CONSTRAINT chk_usuario_perfil CHECK (perfil IN ('ADMIN','EMPRESARIO','ARTESAO'));
ALTER TABLE usuario DROP CHECK chk_usuario_perfil_confeccao;
ALTER TABLE usuario ADD CONSTRAINT chk_usuario_perfil_confeccao CHECK (
 (perfil='ADMIN' AND id_confeccao IS NULL) OR
 (perfil='EMPRESARIO' AND id_confeccao IS NOT NULL) OR
 (perfil='ARTESAO' AND id_confeccao IS NULL)
);
CREATE TABLE artesao (
 id_artesao BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 nome VARCHAR(120) NOT NULL,
 cpf VARCHAR(11) NOT NULL UNIQUE,
 telefone VARCHAR(20) NOT NULL,
 whatsapp VARCHAR(20) NOT NULL,
 email VARCHAR(160) NULL
);
