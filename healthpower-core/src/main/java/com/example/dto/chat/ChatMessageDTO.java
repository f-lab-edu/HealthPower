package com.example.dto.chat;

import com.example.entity.chat.ChatMessage;
import com.example.entity.chat.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long roomId;
    private String senderId;
    private String receiverId;
    private String senderNickname;
    private String content;
    private String imageUrl;
    private String token;
    private MessageType type;
    private LocalDateTime timeStamp;
    private com.example.enumpackage.ChatType chatType;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
    /* 엔티티 변환 */
    public ChatMessage toEntity(ChatRoom room) {
        return ChatMessage.builder()
                .chatRoom(room)           // ★ 객체 주입
                .senderId(senderId)
                .content(content)
                .chatType(chatType)
                .build();
    }
}
