package br.com.trama.service;

import br.com.trama.security.UsuarioPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final Key signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${trama.jwt.secret}") String secret,
            @Value("${trama.jwt.expiration-ms:86400000}") long expirationMs) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("TRAMA_JWT_SECRET precisa ter pelo menos 32 bytes.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String gerarToken(UsuarioPrincipal usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("uid", usuario.getIdUsuario())
                .claim("perfil", usuario.getPerfil().name())
                .claim("idConfeccao", usuario.getIdConfeccao())
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public String obterEmail(String token) {
        return claims(token).getSubject();
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        try {
            Claims claims = claims(token);
            return userDetails.getUsername().equalsIgnoreCase(claims.getSubject())
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims claims(String token) {
        return Jwts.parser().verifyWith((javax.crypto.SecretKey) signingKey)
                .build().parseSignedClaims(token).getPayload();
    }
}
