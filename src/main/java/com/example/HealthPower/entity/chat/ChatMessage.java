package com.example.HealthPower.entity.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderId;
    private String receiverId;

    @Lob //대형 객체 데이터를 저장하기 위한 가변 길이 데이터 유형
    private String content;

    private String roomId;
    private LocalDateTime sentAt = LocalDateTime.now();

    public ChatMessage(String roomId, String senderId, String receiverId, String content) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }
}

