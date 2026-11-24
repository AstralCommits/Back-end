package br.com.trama.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Avaliação de um pedido entregue, mapeada para avaliacoes_artesao.
 * A coluna artesao_id (nome definido pelo banco) guarda o id da confecção do pedido (FK composta com pedido no banco).
 */
@Entity
@Table(name = "avaliacoes_artesao",
        uniqueConstraints = @UniqueConstraint(name = "uq_avaliacao_pedido", columnNames = "pedido_id"))
public class AvaliacaoConfeccao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "artesao_id", nullable = false)
    private Long confeccaoId;

    @NotNull
    @Min(value = 1, message = "A nota deve ser de 1 a 5.")
    @Max(value = 5, message = "A nota deve ser de 1 a 5.")
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Size(max = 500)
    @Column(name = "comentario", length = 500)
    private String comentario;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    public AvaliacaoConfeccao() {}

    @PrePersist
    void definirData() {
        if (dataAvaliacao == null) {
            dataAvaliacao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public Long getConfeccaoId() { return confeccaoId; }
    public void setConfeccaoId(Long confeccaoId) { this.confeccaoId = confeccaoId; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
}
