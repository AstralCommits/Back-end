-- Issue 04: catálogo, cotações corporativas, itens e notificações.
CREATE TABLE produto (
 id_produto BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 nome VARCHAR(120) NOT NULL,
 descricao VARCHAR(500) NULL,
 preco DECIMAL(12,2) NOT NULL,
 id_artesao BIGINT NOT NULL,
 CONSTRAINT fk_produto_artesao FOREIGN KEY (id_artesao) REFERENCES artesao(id_artesao),
 CONSTRAINT chk_produto_preco CHECK (preco > 0)
);
CREATE TABLE cotacao_corporativa (
 id_cotacao BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 prazo_entrega DATE NOT NULL,
 status VARCHAR(40) NOT NULL,
 data_criacao DATETIME NOT NULL,
 id_usuario INT NOT NULL,
 CONSTRAINT fk_cotacao_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
 CONSTRAINT chk_cotacao_status CHECK (status IN ('EM_ANALISE_ARTESAO'))
);
CREATE TABLE item_cotacao (
 id_item BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 id_cotacao BIGINT NOT NULL,
 id_produto BIGINT NOT NULL,
 CONSTRAINT fk_item_cotacao FOREIGN KEY (id_cotacao) REFERENCES cotacao_corporativa(id_cotacao),
 CONSTRAINT fk_item_produto FOREIGN KEY (id_produto) REFERENCES produto(id_produto),
 CONSTRAINT uq_item_cotacao_produto UNIQUE (id_cotacao,id_produto)
);
CREATE TABLE notificacao_artesao (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 id_artesao BIGINT NOT NULL,
 id_cotacao BIGINT NOT NULL,
 mensagem VARCHAR(300) NOT NULL,
 data_criacao DATETIME NOT NULL,
 CONSTRAINT fk_notificacao_artesao FOREIGN KEY (id_artesao) REFERENCES artesao(id_artesao),
 CONSTRAINT fk_notificacao_cotacao FOREIGN KEY (id_cotacao) REFERENCES cotacao_corporativa(id_cotacao)
);
