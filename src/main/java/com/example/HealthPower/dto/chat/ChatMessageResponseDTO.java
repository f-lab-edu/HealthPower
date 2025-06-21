package com.example.HealthPower.dto.chat;

import com.example.HealthPower.chatType.ChatType;
import com.example.HealthPower.entity.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class ChatMessageResponseDTO {
    private String roomId;
    private String senderId;
    private String senderNickname;
    private String content;
    private ChatType type;
    private LocalDateTime sentAt;

    /* ── 엔티티 → DTO 변환 헬퍼 ── */
    public static ChatMessageResponseDTO from(ChatMessage m) {
        return of(
                m.getRoomId(),
                m.getSenderId(),
                m.getSenderNickname(),
                m.getContent(),
                m.getChatType(),
                m.getSentAt()
        );
    }
}
