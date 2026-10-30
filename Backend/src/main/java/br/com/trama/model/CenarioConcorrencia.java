package br.com.trama.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cenario_concorrencia")
public class CenarioConcorrencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cenario")
    private Long idCenario;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "percentual_impacto", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualImpacto;

    @Column(name = "descricao", length = 300)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    public CenarioConcorrencia() {}

    public Long getIdCenario() { return idCenario; }
    public void setIdCenario(Long idCenario) { this.idCenario = idCenario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPercentualImpacto() { return percentualImpacto; }
    public void setPercentualImpacto(BigDecimal percentualImpacto) { this.percentualImpacto = percentualImpacto; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
