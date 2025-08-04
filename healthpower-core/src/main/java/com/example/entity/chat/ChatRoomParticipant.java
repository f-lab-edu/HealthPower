package com.example.entity.chat;

import com.example.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "chat_room_participant")
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @JsonBackReference
    private ChatRoom chatRoom;

    //User엔티티의 userId는 String 타입이므로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    private boolean owner;

    private boolean exited;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public static ChatRoomParticipant of(ChatRoom room, User user, boolean owner) {
        ChatRoomParticipant chatRoomParticipant = new ChatRoomParticipant();
        chatRoomParticipant.chatRoom = room;
        chatRoomParticipant.user = user;
        chatRoomParticipant.owner = owner;
        chatRoomParticipant.exited = false;
        return chatRoomParticipant;
    }

    public ChatRoomParticipant(ChatRoom room, User user) {
        this.chatRoom = room;
        this.user = user;
    }
}
