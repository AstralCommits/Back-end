package br.com.trama.error;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message, String path) {
}
