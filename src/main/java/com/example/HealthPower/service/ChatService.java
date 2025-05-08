package com.example.HealthPower.service;

import com.example.HealthPower.dto.chat.ChatMessageDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.chat.ChatMessage;
import com.example.HealthPower.entity.chat.ChatRoom;
import com.example.HealthPower.repository.ChatMessageRepository;
import com.example.HealthPower.repository.ChatRoomRepository;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // 두 사용자 간의 고정된 채팅방 생성 or 조회
    public ChatRoom createRoomId(String userA, String userB) {
        //List<String> users = Arrays.asList(userA, userB); // 정렬: A-B와 B-A를 통일
        //Collections.sort(users);
        //return users.get(0) + "_" + users.get(1);

        return chatRoomRepository.findByParticipants(userA, userB)
                .orElseGet(()->{
                    ChatRoom room = new ChatRoom(userA, userB); //기존 채팅방이 없다면 새로 생성
                    return chatRoomRepository.save(room);
                });
    }

    public void save(ChatMessageDTO chatMessageDTO) {
        ChatMessage message = new ChatMessage(
                chatMessageDTO.getRoomId(),
                chatMessageDTO.getSenderId(),
                chatMessageDTO.getReceiverId(),
                chatMessageDTO.getContent()
        );

        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(String roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }

    // 사용자가 속한 모든 채팅방 조회
    public List<ChatRoom> getRoomsByUser(String userId) {
        return chatRoomRepository.findAll().stream()
                .filter(room -> room.getParticipantA().equals(userId) || room.getParticipantB().equals(userId))
                .toList();
    }

    public User getByUserId(String userId) {
        return userRepository.findByUserId(userId).
                orElseThrow(() -> new UsernameNotFoundException("존재하는 아이디가 없음"));
    }
}
