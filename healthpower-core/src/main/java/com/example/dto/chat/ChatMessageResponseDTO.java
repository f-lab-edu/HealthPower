package com.example.dto.chat;

import com.example.entity.chat.ChatMessage;
import com.example.enumpackage.ChatType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class ChatMessageResponseDTO {
    private Long roomId;
    private String senderId;
    private String content;
    private ChatType type;
    private LocalDateTime sentAt;

    /* ── 엔티티 → DTO 변환 헬퍼 ── */
    public static ChatMessageResponseDTO from(ChatMessage m) {
        return of(
                m.getChatRoom().getRoomId(),
                m.getSenderId(),
                m.getContent(),
                m.getChatType(),
                m.getSentAt()
        );
    }
}
