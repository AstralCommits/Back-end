package br.com.trama.service;

import br.com.trama.model.Categoria;
import br.com.trama.model.Cidade;
import br.com.trama.model.Usuario;
import br.com.trama.model.enums.PerfilUsuario;
import br.com.trama.repository.UsuarioRepository;
import br.com.trama.repository.CategoriaRepository;
import br.com.trama.repository.CidadeRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Seeds the small reference lists required to start using the API. */
@Configuration
@Profile({"local", "test"})
public class DadosIniciaisService {
    @Bean
    CommandLineRunner garantirDadosDeReferencia(
            CidadeRepository cidadeRepository,
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${trama.local.admin-email:}") String adminEmail,
            @Value("${trama.local.admin-password:}") String adminPassword) {
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

            // Conta local opcional: só é criada quando as credenciais são fornecidas por variáveis de ambiente.
            if (adminEmail != null && !adminEmail.isBlank()
                    && adminPassword != null && !adminPassword.isBlank()
                    && usuarioRepository.findByEmailIgnoreCase(adminEmail).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador local");
                admin.setEmail(adminEmail.trim());
                admin.setSenhaHash(passwordEncoder.encode(adminPassword));
                admin.setPerfil(PerfilUsuario.ADMIN);
                usuarioRepository.save(admin);
            }
        };
    }
}
