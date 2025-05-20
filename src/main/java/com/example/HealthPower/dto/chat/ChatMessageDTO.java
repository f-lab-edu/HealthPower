package com.example.HealthPower.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private String senderId;
    private String receiverId;
    private String senderNickname;
    private String content;
    private String roomId;
    private String photoUrl;
    private String token;
    private MessageType type;
    private LocalDateTime timeStamp;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
