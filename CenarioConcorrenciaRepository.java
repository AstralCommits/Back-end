package br.com.trama.repository;

import br.com.trama.model.CenarioConcorrencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CenarioConcorrenciaRepository extends JpaRepository<CenarioConcorrencia, Long> {
    List<CenarioConcorrencia> findAllByUsuario_IdUsuarioOrderByIdCenarioDesc(Long idUsuario);
}
