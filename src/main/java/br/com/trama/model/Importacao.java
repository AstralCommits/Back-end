package br.com.trama.model;

import br.com.trama.model.enums.StatusImportacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "importacao")
public class Importacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_importacao")
    private Long idImportacao;

    @Column(name = "nome_arquivo", nullable = false, length = 150)
    private String nomeArquivo;

    @Column(name = "data_importacao", nullable = false)
    private LocalDateTime dataImportacao;

    @Column(name = "qtd_linhas")
    private Integer qtdLinhas;

    @Enumerated(EnumType.STRING)
    @Column(name = "imp_status", nullable = false, length = 10)
    private StatusImportacao status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public Importacao() {}
    public Long getIdImportacao() { return idImportacao; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public LocalDateTime getDataImportacao() { return dataImportacao; }
    public void setDataImportacao(LocalDateTime dataImportacao) { this.dataImportacao = dataImportacao; }
    public Integer getQtdLinhas() { return qtdLinhas; }
    public void setQtdLinhas(Integer qtdLinhas) { this.qtdLinhas = qtdLinhas; }
    public StatusImportacao getStatus() { return status; }
    public void setStatus(StatusImportacao status) { this.status = status; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
