package br.com.trama.model;

import br.com.trama.model.enums.PorteConfeccao;
import br.com.trama.model.enums.TipoConfeccao;
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

@Entity
@Table(name = "confeccao")
public class Confeccao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_confeccao")
    private Long idConfeccao;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "cnpj", unique = true, length = 14)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(name = "porte", nullable = false, length = 10)
    private PorteConfeccao porte;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoConfeccao tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cidade", nullable = false)
    private Cidade cidade;

    public Confeccao() {}

    public Long getIdConfeccao() { return idConfeccao; }
    public void setIdConfeccao(Long idConfeccao) { this.idConfeccao = idConfeccao; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public PorteConfeccao getPorte() { return porte; }
    public void setPorte(PorteConfeccao porte) { this.porte = porte; }
    public TipoConfeccao getTipo() { return tipo; }
    public void setTipo(TipoConfeccao tipo) { this.tipo = tipo; }
    public Cidade getCidade() { return cidade; }
    public void setCidade(Cidade cidade) { this.cidade = cidade; }
}
