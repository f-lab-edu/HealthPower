package com.example.repository;

import com.example.entity.chat.ChatMessage;
import com.example.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    void deleteByChatRoom(ChatRoom chatRoom);

}