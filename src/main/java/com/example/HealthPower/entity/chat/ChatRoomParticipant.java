package com.example.HealthPower.entity.chat;

import com.example.HealthPower.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "exited")
    private boolean exited = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public static ChatRoomParticipant of(ChatRoom room, User user, boolean exited) {
        ChatRoomParticipant chatRoomParticipant = new ChatRoomParticipant();
        chatRoomParticipant.chatRoom = room;
        chatRoomParticipant.user = user;
        chatRoomParticipant.exited = exited;
        return chatRoomParticipant;
    }

    public ChatRoomParticipant(ChatRoom room, User user) {
        this.chatRoom = room;
        this.user = user;
    }
}
