package com.example.HealthPower.dto.chat;

import lombok.Getter;

@Getter
public class ChatMessageDTO {
    private String senderId;
    private String receiverId;
    private String content;
    private String roomId;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
