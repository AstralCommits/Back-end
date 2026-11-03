package br.com.trama.service;

import br.com.trama.dto.LoginRequest;
import br.com.trama.dto.LoginResponse;
import br.com.trama.error.BadRequestException;
import br.com.trama.model.Usuario;
import br.com.trama.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(() ->
                        new BadRequestException("Usuário ou senha inválidos."));

        String token =
                jwtService.gerarToken(usuario.getEmail());

        return new LoginResponse(token);
    }
}
