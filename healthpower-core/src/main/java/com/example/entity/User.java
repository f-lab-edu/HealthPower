package com.example.entity;

import com.example.entity.chat.ChatRoom;
import com.example.entity.chat.ChatRoomParticipant;
import com.example.enumpackage.Gender;
import com.example.enumpackage.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "\"user\"")  // ← 여기만 바꿔주면 끝
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User /*implements UserDetails*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "balance")
    private Long balance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //Ordinal을 사용 X
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChatRoomParticipant> chatRoomParticipants = new ArrayList<>();

    public void deductBalance(int amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("잔액 부족");
        }
        this.balance -= amount;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRoomParticipants.stream()
                .filter(participant -> !participant.isExited()) // 나간 방 제외하고 싶다면 추가
                .map(ChatRoomParticipant::getChatRoom)
                .collect(Collectors.toList());
    }

}
