package com.example.entity.chat;

import com.example.dto.chat.ChatMessageDTO;
import com.example.enumpackage.ChatType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id")
    private String senderId;

    @Lob //대형 객체 데이터를 저장하기 위한 가변 길이 데이터 유형
    private String content;

    @Column(name = "sender_nickname")
    private String senderNickname;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // fk 타입 = BIGINT
    //@JoinColumn(name = "chat_room_id", nullable = false)
    @JoinColumn(name = "roomId", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    private com.example.enumpackage.ChatType chatType;

    //public ChatMessage(String roomId, String senderId, String receiverId, String content) {
    public ChatMessage(String senderId, String content, ChatRoom chatRoom) {
        this.senderId = senderId;
        this.content = content;
        this.chatRoom = chatRoom;
    }

    public ChatMessage(String senderId, String content, LocalDateTime timeStamp, ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = timeStamp;
    }

    /* ----------  정적 팩토리 ---------- */

    // 시스템(ENTER / EXIT) 메시지
    public static ChatMessage systemMessage(
            ChatRoom chatRoom,
            String text,
            String senderNickname,
            String senderId,
            ChatType chatType) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(text)
                .senderNickname(senderNickname)
                .senderId(senderId)
                .sentAt(LocalDateTime.now())
                .chatType(chatType)
                .build();
    }

    // 일반 TALK 메시지
    public static ChatMessage userMessage(ChatMessageDTO chatMessageDTO, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)// ★ FK 세팅
                .senderId(chatMessageDTO.getSenderId())
                .content(chatMessageDTO.getContent())
                .chatType(chatMessageDTO.getChatType())
                .sentAt(chatMessageDTO.getTimeStamp())
                .build();
    }
}

