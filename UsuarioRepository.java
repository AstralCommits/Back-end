package br.com.trama.repository;

import br.com.trama.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = "confeccao")
    Optional<Usuario> findByEmailIgnoreCase(String email);
}
