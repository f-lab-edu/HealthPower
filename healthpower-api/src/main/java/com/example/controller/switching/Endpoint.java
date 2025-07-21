package com.example.controller.switching;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Endpoint {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
