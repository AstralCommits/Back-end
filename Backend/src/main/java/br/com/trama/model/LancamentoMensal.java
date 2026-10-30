package br.com.trama.model;

import br.com.trama.model.enums.OrigemDado;
import br.com.trama.model.enums.OrigemLancamento;
import br.com.trama.model.enums.StatusLancamento;
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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamento_mensal", uniqueConstraints = @UniqueConstraint(
        name = "uk_lancamento_confeccao_categoria_mes",
        columnNames = {"id_confeccao", "id_categoria", "mes_referencia"}))
public class LancamentoMensal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lancamento")
    private Long idLancamento;

    @Column(name = "mes_referencia", nullable = false)
    private LocalDate mesReferencia;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "faturamento", nullable = false, precision = 12, scale = 2)
    private BigDecimal faturamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false, length = 10)
    private OrigemLancamento origem;

    @Enumerated(EnumType.STRING)
    @Column(name = "lanc_status", nullable = false, length = 10)
    private StatusLancamento status = StatusLancamento.ATIVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_confeccao", nullable = false)
    private Confeccao confeccao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    // Mantido como FK escalar para compatibilidade com a tabela importacao existente.
    @Column(name = "id_importacao")
    private Long idImportacao;

    // Procedência da informação (diferente de origem MANUAL/IMPORTACAO do registro).
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_dado", nullable = false, length = 30)
    private OrigemDado origemDado;

    @Column(name = "nome_fonte", nullable = false, length = 200)
    private String nomeFonte;

    public LancamentoMensal() {}

    public Long getIdLancamento() { return idLancamento; }
    public void setIdLancamento(Long idLancamento) { this.idLancamento = idLancamento; }
    public LocalDate getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(LocalDate mesReferencia) { this.mesReferencia = mesReferencia; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getFaturamento() { return faturamento; }
    public void setFaturamento(BigDecimal faturamento) { this.faturamento = faturamento; }
    public OrigemLancamento getOrigem() { return origem; }
    public void setOrigem(OrigemLancamento origem) { this.origem = origem; }
    public StatusLancamento getStatus() { return status; }
    public void setStatus(StatusLancamento status) { this.status = status; }
    public Confeccao getConfeccao() { return confeccao; }
    public void setConfeccao(Confeccao confeccao) { this.confeccao = confeccao; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public Long getIdImportacao() { return idImportacao; }
    public void setIdImportacao(Long idImportacao) { this.idImportacao = idImportacao; }
    public OrigemDado getOrigemDado() { return origemDado; }
    public void setOrigemDado(OrigemDado origemDado) { this.origemDado = origemDado; }
    public String getNomeFonte() { return nomeFonte; }
    public void setNomeFonte(String nomeFonte) { this.nomeFonte = nomeFonte; }
}
