package com.example.HealthPower.entity.chat;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ParticipantId implements Serializable {
    private Long roomId;
    private String userId;
}
