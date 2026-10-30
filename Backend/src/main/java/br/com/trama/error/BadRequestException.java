package br.com.trama.error;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
