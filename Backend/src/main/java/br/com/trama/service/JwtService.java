package br.com.trama.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String SECRET =
            "trama-backend-chave-secreta-jwt-2026-seguranca";

    private Key getKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(String email) {

        Date agora = new Date();

        Date expiracao =
                new Date(agora.getTime() + 86400000);

        return Jwts.builder()
                .subject(email)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getKey())
                .compact();
    }
}
