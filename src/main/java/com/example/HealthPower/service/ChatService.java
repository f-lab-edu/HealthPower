package com.example.HealthPower.service;

import com.example.HealthPower.dto.chat.ChatMessageDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.chat.ChatMessage;
import com.example.HealthPower.entity.chat.ChatRoom;
import com.example.HealthPower.entity.chat.ChatRoomParticipant;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.ChatMessageRepository;
import com.example.HealthPower.repository.ChatRoomParticipantRepository;
import com.example.HealthPower.repository.ChatRoomRepository;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;

    // 두 사용자 간의 고정된 채팅방 생성 or 조회
    public ChatRoom createRoomId(String userA, String userB) {
        return chatRoomRepository.findByParticipants(userA, userB)
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom(userA, userB);
                    ChatRoom savedRoom = chatRoomRepository.save(room);

                    //사용자 A 참여자 정보 저장
                    User userEntityA = userRepository.findByUserId(userA).orElseThrow();
                    ChatRoomParticipant participantA = new ChatRoomParticipant();
                    participantA.setUser(userEntityA);
                    participantA.setChatRoom(savedRoom);
                    participantA.setExited(false);
                    chatRoomParticipantRepository.save(participantA);

                    //사용자 B 참여자 정보 저장
                    User userEntityB = userRepository.findByUserId(userB).orElseThrow();
                    ChatRoomParticipant participantB = new ChatRoomParticipant();
                    participantB.setUser(userEntityB);
                    participantB.setChatRoom(savedRoom);
                    participantB.setExited(false);
                    chatRoomParticipantRepository.save(participantB);

                    return savedRoom;
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
        return chatRoomParticipantRepository.findByUser_UserIdAndExitedFalse(userId)
                .stream()
                .map(ChatRoomParticipant::getChatRoom)
                .toList();
    }

    public User getByUserId(String userId) {
        return userRepository.findByUserId(userId).
                orElseThrow(() -> new UsernameNotFoundException("존재하는 아이디가 없음"));
    }

    public void markUserExited(String roomId, String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));

        ChatRoom room = chatRoomRepository.findByRoomId(roomId).orElseThrow();
        ChatRoomParticipant participant = chatRoomParticipantRepository.findByChatRoomAndUser(room, user).orElseThrow();
        participant.setExited(true);
        chatRoomParticipantRepository.save(participant);

        boolean allExited = chatRoomParticipantRepository.findByChatRoom(room).stream()
                .allMatch(ChatRoomParticipant::isExited);

        if (allExited) {
            chatMessageRepository.deleteByChatRoom(room);
            chatRoomParticipantRepository.deleteByChatRoom(room);
            chatRoomRepository.delete(room);
        }
    }

}
