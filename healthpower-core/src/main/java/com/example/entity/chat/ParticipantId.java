package com.example.entity.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ParticipantId implements Serializable {

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "user_id")
    private String userId;
}
