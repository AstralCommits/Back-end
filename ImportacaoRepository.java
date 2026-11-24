package br.com.trama.repository;

import br.com.trama.model.Importacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportacaoRepository extends JpaRepository<Importacao, Long> {
    List<Importacao> findAllByUsuario_IdUsuarioOrderByDataImportacaoDesc(Long idUsuario);
}
