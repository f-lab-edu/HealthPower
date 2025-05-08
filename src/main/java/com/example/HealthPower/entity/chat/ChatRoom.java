package com.example.HealthPower.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {
    @Id
    private String roomId;
    private String participantA;
    private String participantB;

    public ChatRoom(String participantA, String participantB) {
        if (participantA.compareTo(participantB) < 0) { //participantA < participantB
            this.participantA = participantA;
            this.participantB = participantB;
        } else {
            this.participantA = participantB;
            this.participantB = participantA;
        }
        this.roomId = this.participantA + "_" + this.participantB;
    }
}
