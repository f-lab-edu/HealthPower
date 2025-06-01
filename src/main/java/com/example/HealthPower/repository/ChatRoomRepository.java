package com.example.HealthPower.repository;

import com.example.HealthPower.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    Optional<ChatRoom> findByParticipantAAndParticipantB(String participantA, String participantB);
    Optional<ChatRoom> findByParticipantBAndParticipantA(String participantB, String participantA);
    Optional<ChatRoom> findByRoomId(String roomId);
    /*Optional<ChatRoom> findByRoom(ChatRoom chatRoom);*/

    default Optional<ChatRoom> findByParticipants(String user1, String user2) {
        if (user1.compareTo(user2) < 0) {
            return findByParticipantBAndParticipantA(user1, user2);
        } else {
            return findByParticipantAAndParticipantB(user2, user1);
        }
    }
}
