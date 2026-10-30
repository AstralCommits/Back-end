package br.com.trama.service;

import br.com.trama.model.Categoria;
import br.com.trama.model.Cidade;
import br.com.trama.repository.CategoriaRepository;
import br.com.trama.repository.CidadeRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Seeds the small reference lists required to start using the API. */
@Configuration
public class DadosIniciaisService {
    @Bean
    CommandLineRunner garantirDadosDeReferencia(CidadeRepository cidadeRepository, CategoriaRepository categoriaRepository) {
        return args -> {
            List<String> cidades = List.of("Toritama", "Caruaru", "Santa Cruz do Capibaribe");
            for (String nome : cidades) {
                if (cidadeRepository.findByNomeIgnoreCaseAndUf(nome, "PE").isEmpty()) {
                    cidadeRepository.save(new Cidade(nome, "PE"));
                }
            }

            List<String> categorias = List.of("Moda Feminina", "Moda Masculina", "Moda Infantil", "Fitness", "Praia");
            for (String nome : categorias) {
                if (categoriaRepository.findByNomeIgnoreCase(nome).isEmpty()) {
                    categoriaRepository.save(new Categoria(nome));
                }
            }
        };
    }
}
