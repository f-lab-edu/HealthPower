package com.example.HealthPower.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(roomId, chatRoom.roomId) && Objects.equals(participantA, chatRoom.participantA) && Objects.equals(participantB, chatRoom.participantB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, participantA, participantB);
    }
}
