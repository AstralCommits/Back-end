package br.com.trama.repository;
import br.com.trama.model.Artesao;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ArtesaoRepository extends JpaRepository<Artesao,Long> { boolean existsByCpf(String cpf); }
