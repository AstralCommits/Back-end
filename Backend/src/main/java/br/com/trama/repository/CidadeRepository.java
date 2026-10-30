package br.com.trama.repository;

import br.com.trama.model.Cidade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    Optional<Cidade> findByNomeIgnoreCaseAndUf(String nome, String uf);
}
