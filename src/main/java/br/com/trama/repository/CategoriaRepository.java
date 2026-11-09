package br.com.trama.repository;

import br.com.trama.model.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdCategoriaNot(String nome, Long idCategoria);
    Optional<Categoria> findByNomeIgnoreCase(String nome);
    List<Categoria> findAllByOrderByNomeAsc();
}
