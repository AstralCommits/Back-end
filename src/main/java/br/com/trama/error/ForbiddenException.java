package br.com.trama.error;

/** Usuário autenticado sem permissão sobre o recurso (HTTP 403). */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
