package com.example.HealthPower.entity.chat;

import com.example.HealthPower.chatType.ChatType;
import com.example.HealthPower.dto.chat.ChatMessageDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    private String senderId;

    private String receiverId;

    @Lob //대형 객체 데이터를 저장하기 위한 가변 길이 데이터 유형
    private String content;

    private String senderNickname;

    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // fk 타입 = BIGINT
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    //public ChatMessage(String roomId, String senderId, String receiverId, String content) {
    public ChatMessage(String senderId, String receiverId, String content, ChatRoom chatRoom) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.chatRoom = chatRoom;
    }

    public ChatMessage(String roomId, String senderId, String receiverId, String content, LocalDateTime timeStamp, ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = timeStamp;
    }

    /* ----------  정적 팩토리 ---------- */

    // 시스템(ENTER / EXIT) 메시지
    public static ChatMessage systemMessage(
            ChatRoom chatRoom,
            String text,
            ChatType chatType) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .roomId(chatRoom.getRoomId())
                .content(text)
                .chatType(chatType)
                .build();
    }

    // 일반 TALK 메시지
    public static ChatMessage userMessage(ChatMessageDTO chatMessageDTO, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)                     // ★ FK 세팅
                .roomId(chatRoom.getRoomId())
                .senderId(chatMessageDTO.getSenderId())
                .receiverId(chatMessageDTO.getReceiverId())
                .content(chatMessageDTO.getContent())
                .chatType(chatMessageDTO.getChatType())
                .build();
    }
}

