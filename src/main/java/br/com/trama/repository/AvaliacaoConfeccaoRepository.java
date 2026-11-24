package br.com.trama.repository;

import br.com.trama.model.AvaliacaoConfeccao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoConfeccaoRepository extends JpaRepository<AvaliacaoConfeccao, Long> {
    boolean existsByPedidoId(Long pedidoId);
}
