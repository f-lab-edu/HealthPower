package com.example.controller.web;

import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class HealthForwardController {

    private final WebClient webClient;

    public HealthForwardController(WebClient webClient) {
        this.webClient = WebClient.builder().baseUrl("http://localhost:8080/actuator").build();
    }

    @GetMapping("/healthpower")
    public Mono<ResponseEntity<String>> forwardHealth() {
        return webClient.get()
                .uri("/health")
                .retrieve()
                .toEntity(String.class);
    }
}
