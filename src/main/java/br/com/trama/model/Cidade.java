package br.com.trama.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cidade")
public class Cidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cidade")
    private Long idCidade;

    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    public Cidade() {}

    public Cidade(String nome, String uf) {
        this.nome = nome;
        this.uf = uf;
    }

    public Long getIdCidade() { return idCidade; }
    public void setIdCidade(Long idCidade) { this.idCidade = idCidade; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
}
