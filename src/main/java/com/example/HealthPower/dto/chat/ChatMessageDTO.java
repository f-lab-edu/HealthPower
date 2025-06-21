package com.example.HealthPower.dto.chat;

import com.example.HealthPower.chatType.ChatType;
import com.example.HealthPower.entity.chat.ChatMessage;
import com.example.HealthPower.entity.chat.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private String roomId;
    private String senderId;
    private String receiverId;
    private String senderNickname;
    private String content;
    private String photoUrl;
    private String token;
    private MessageType type;
    private LocalDateTime timeStamp;
    private ChatType chatType;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
    /* 엔티티 변환 */
    public ChatMessage toEntity(ChatRoom room) {
        return ChatMessage.builder()
                .roomId(room.getRoomId())           // ★ 객체 주입
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .chatType(chatType)
                .senderNickname(senderNickname)
                .build();
    }
}
