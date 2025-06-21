package com.example.HealthPower.entity.chat;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_private_pari", columnNames = {"participantA", "participantB"}))
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //숫자 pk 유지

    @Column(nullable = false, unique = true)
    private String roomId; // userA_userB 구조

    @Column(nullable = false)
    private String participantA;

    @Column(nullable = false)
    private String participantB;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @UpdateTimestamp
    private LocalDateTime updatedAt;

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
