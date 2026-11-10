package br.com.trama.controller;

import java.util.Map;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/api/health")
    @SecurityRequirements
    public Map<String, String> health() {
        return Map.of("status", "UP", "application", "TRAMA Backend");
    }
}
