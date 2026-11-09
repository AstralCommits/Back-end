package br.com.trama.repository;

import br.com.trama.model.Confeccao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfeccaoRepository extends JpaRepository<Confeccao, Long> {
    List<Confeccao> findAllByOrderByNomeAsc();
}
