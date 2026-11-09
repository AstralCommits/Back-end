package br.com.trama.service;

import br.com.trama.dto.LoginRequest;
import br.com.trama.dto.LoginResponse;
import br.com.trama.security.UsuarioPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        UsuarioPrincipal usuario = (UsuarioPrincipal) authentication.getPrincipal();
        return new LoginResponse(jwtService.gerarToken(usuario));
    }
}
