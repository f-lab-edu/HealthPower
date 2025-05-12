package com.example.HealthPower.service;

import com.example.HealthPower.dto.chat.ChatMessageDTO;
import com.example.HealthPower.dto.chat.ChatRoomListItemDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;

    // 두 사용자 간의 고정된 채팅방 생성 or 조회
    public ChatRoom createRoomId(String userA, String userB) {

        Optional<ChatRoom> optionalRoom = chatRoomRepository.findByParticipants(userA, userB);

        if (optionalRoom.isPresent()) {
            ChatRoom room = optionalRoom.get();

            // 사용자가 나간 상태인지 확인
            User userEntityA = userRepository.findByUserId(userA).orElseThrow();
            User userEntityB = userRepository.findByUserId(userB).orElseThrow();

            ChatRoomParticipant participantA = chatRoomParticipantRepository
                    .findByChatRoomAndUser(room, userEntityA)
                    .orElseThrow();
            ChatRoomParticipant participantB = chatRoomParticipantRepository
                    .findByChatRoomAndUser(room, userEntityB)
                    .orElseThrow();

            // 둘 중 한 명이라도 나갔다면 입장 금지
            if (participantA.isExited() || participantB.isExited()) {
                throw new IllegalStateException("이미 나간 채팅방입니다.");
            }

            return room; // 방은 있고, 참여도 가능
        }

        //여기서 추가 방어: 이전에 참가자 기록이 존재했던 방이 있었는지 확인
        String roomId = new ChatRoom(userA, userB).getRoomId();
        boolean hadHistory = chatRoomParticipantRepository.findByChatRoom(new ChatRoom(userA, userB)).size() > 0; // 방은 없어도 기록이 있을 수 있음

        if (hadHistory) {
            throw new IllegalStateException("이미 존재했던 채팅방입니다. 입장할 수 없습니다");
        }

        // 방이 없는 경우에만 새로 생성
        ChatRoom room = new ChatRoom(userA, userB);
        ChatRoom savedRoom = chatRoomRepository.save(room);

        User userEntityA = userRepository.findByUserId(userA).orElseThrow();
        User userEntityB = userRepository.findByUserId(userB).orElseThrow();

        ChatRoomParticipant participantA = new ChatRoomParticipant();
        participantA.setUser(userEntityA);
        participantA.setChatRoom(savedRoom);
        participantA.setExited(false);
        chatRoomParticipantRepository.save(participantA);

        ChatRoomParticipant participantB = new ChatRoomParticipant();
        participantB.setUser(userEntityB);
        participantB.setChatRoom(savedRoom);
        participantB.setExited(false);
        chatRoomParticipantRepository.save(participantB);

        return savedRoom;

        //기존 소스
        /*return chatRoomRepository.findByParticipants(userA, userB)
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
                });*/
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
    public List<ChatRoomListItemDTO> getRoomsByUser(String userId) {

        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByUser_UserId(userId);

        // 🔍 디버깅 로그 추가
        System.out.println("🧪 참여자 수: " + participants.size());
        participants.forEach(p -> {
            try {
                System.out.println("🧪 방 ID: " + p.getChatRoom().getRoomId() + ", exited: " + p.isExited());
            } catch (Exception e) {
                System.out.println("❌ 방 ID를 조회할 수 없음 (chatRoom null): " + e.getMessage());
            }
        });

        return chatRoomParticipantRepository.findByUser_UserId(userId)
                .stream()
                .map(p -> new ChatRoomListItemDTO(p.getChatRoom(), p.isExited()))
                .toList();
    }

    public User getByUserId(String userId) {
        return userRepository.findByUserId(userId).
                orElseThrow(() -> new UsernameNotFoundException("존재하는 아이디가 없음"));
    }

    @Transactional
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

    public boolean hasUserActiveInRoom(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("방이 존재하지 않습니다"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        return chatRoomParticipantRepository.findByChatRoomAndUser(room, user)
                .filter(participant -> !participant.isExited()) // 나간 상태가 아니어야 입장 허용
                .isPresent();
    }

}
