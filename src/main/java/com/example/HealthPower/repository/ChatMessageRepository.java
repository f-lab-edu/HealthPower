package com.example.HealthPower.repository;

import com.example.HealthPower.entity.chat.ChatMessage;
import com.example.HealthPower.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    /*List<ChatMessage> findByRoomId(String roomId);*/

    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    void deleteByChatRoom(ChatRoom chatRoom);

}
