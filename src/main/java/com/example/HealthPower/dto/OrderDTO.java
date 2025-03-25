package com.example.HealthPower.dto;

import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

public class OrderDTO {
    private String userId;

    private String productId;

    private LocalDateTime orderedAt;
}
