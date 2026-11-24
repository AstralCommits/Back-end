package br.com.trama.error;

/** Operação incompatível com o estado atual do recurso (HTTP 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
