package com.example.HealthPower.service;

import com.example.HealthPower.chatType.ChatType;
import com.example.HealthPower.dto.chat.ChatMessageDTO;
import com.example.HealthPower.dto.chat.ChatMessageResponseDTO;
import com.example.HealthPower.dto.chat.ChatRoomListItemDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.chat.ChatMessage;
import com.example.HealthPower.entity.chat.ChatRoom;
import com.example.HealthPower.entity.chat.ChatRoomParticipant;
import com.example.HealthPower.repository.ChatMessageRepository;
import com.example.HealthPower.repository.ChatRoomParticipantRepository;
import com.example.HealthPower.repository.ChatRoomRepository;
import com.example.HealthPower.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;

    // 두 사용자 간의 고정된 채팅방 생성 or 조회
    @Transactional
    public ChatRoom createRoomId(String userA, String userB) {

        if (userA.equals(userB)) {
            throw new IllegalStateException(("자신과 채팅할 수 없습니다."));
        }

        /* 순서 고정 */
        String a = userA.compareTo(userB) < 0 ? userA : userB;
        String b = userA.compareTo(userB) < 0 ? userB : userA;
        String roomId = a + "_" + b;

        ChatRoom room = chatRoomRepository.findByRoomId(roomId).orElse(null);
        if (room != null) {
            verifyNotExited(roomId, userA, userB);
            return room;
        }

        if (chatRoomParticipantRepository.existsByChatRoomRoomId(roomId)) {
            throw new IllegalStateException("이미 존재했던 채팅방입니다. 입장할 수 없습니다.");
        }

        room = chatRoomRepository.save(new ChatRoom(a, b));

        User A = userRepository.findByUserId(userA).orElseThrow();
        User B = userRepository.findByUserId(userB).orElseThrow();

        chatRoomParticipantRepository.save(new ChatRoomParticipant(room, A));
        chatRoomParticipantRepository.save(new ChatRoomParticipant(room, B));

        return room;
    }

    private void verifyNotExited(String roomId, String userA, String userB) {
        ChatRoomParticipant participantA = chatRoomParticipantRepository
                .findByChatRoomRoomIdAndUserUserId(roomId, userA)
                .orElseThrow();
        ChatRoomParticipant participantB = chatRoomParticipantRepository
                .findByChatRoomRoomIdAndUserUserId(roomId, userB)
                .orElseThrow();
        if (participantA.isExited() || participantB.isExited()) {
            throw new IllegalStateException("이미 나간 채팅방입니다.");
        }
    }

    public List<ChatMessage> getMessages(ChatRoom chatRoom) {
        return chatMessageRepository.findByChatRoom(chatRoom);
    }

    public void save(ChatRoom chatRoom, ChatMessageDTO ChatMessageDTO) {

        ChatMessage message = new ChatMessage(
                ChatMessageDTO.getRoomId(),
                ChatMessageDTO.getSenderId(),
                ChatMessageDTO.getReceiverId(),
                ChatMessageDTO.getContent(),
                ChatMessageDTO.getTimeStamp(),
                chatRoom
        );

        chatMessageRepository.save(message);

        System.out.println("띠리링");
    }

    // 사용자가 속한 모든 채팅방 조회
    public List<ChatRoomListItemDTO> getRoomsByUser(String userId) {

        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByUser_UserId(userId);

        return participants
                .stream()
                .map(p -> new ChatRoomListItemDTO(p.getChatRoom(), p.isExited()))
                .toList();
    }

    public User getByUserId(String userId) {
        return userRepository.findByUserId(userId).
                orElseThrow(() -> new UsernameNotFoundException("존재하는 아이디가 없음"));
    }

    @Transactional
    public void markUserExited(ChatRoom chatRoom, String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("유저 없음"));

        /*ChatRoom room = chatRoomRepository.findByRoomId(roomId).orElseThrow();*/
        ChatRoomParticipant participant = chatRoomParticipantRepository.findByChatRoomAndUser(chatRoom, user).orElseThrow();
        participant.setExited(true);
        chatRoomParticipantRepository.save(participant);

        boolean allExited = chatRoomParticipantRepository.findByChatRoom(chatRoom).stream()
                .allMatch(ChatRoomParticipant::isExited);

        if (allExited) {
            chatMessageRepository.deleteByChatRoom(chatRoom);
            chatRoomParticipantRepository.deleteByChatRoom(chatRoom);
            chatRoomRepository.delete(chatRoom);
        }
    }

    public boolean hasUserActiveInRoom(String roomId, String userId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("방이 존재하지 않습니다"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        /*return chatRoomchatRoomParticipantRepositorysitory.findByChatRoomAndUser(room, user)
                .filter(participant -> !participant.isExited()) // 나간 상태가 아니어야 입장 허용
                .isPresent();*/
        return chatRoomParticipantRepository.findByChatRoomRoomIdAndUserUserId(roomId,userId)
                .map(p -> !p.isExited())
                .orElse(false);
    }

    public ChatRoom getChatRoomByRoomId(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다: " + roomId));
    }

    public void handle(ChatMessageDTO chatMessageDTO) {

        ChatRoom room = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방이 없습니다."));

        switch (chatMessageDTO.getChatType()) {
            case ENTER -> enter(chatMessageDTO);
            case TALK -> talk(chatMessageDTO);
            case LEAVE -> leave(chatMessageDTO);
        }
    }
    
     /* ============================================================
                         실제 동작 메서드
       ============================================================ */

    /** 1) 입장 ── 참여자 INSERT + 시스템메시지 브로드캐스트 */
    private void enter(ChatMessageDTO chatMessageDTO) {

        /* ① 이미 들어와 있지 않다면, 참여자 테이블에 insert */
        chatRoomParticipantRepository.findByChatRoomRoomIdAndUserUserId(chatMessageDTO.getRoomId(), chatMessageDTO.getSenderId())
                .orElseGet(() -> {
                    ChatRoom room = chatRoomRepository.getReferenceById(chatMessageDTO.getRoomId());
                    User user = userRepository.getReferenceByUserId(chatMessageDTO.getSenderId());
                    return chatRoomParticipantRepository.save(
                            new ChatRoomParticipant(room, user));   // 생성자 예시
                });

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방이 없습니다"));

        String enterText = chatMessageDTO.getSenderNickname() + "님이 입장했습니다";

        /* ② “OOO님이 입장했습니다” 시스템 메시지 저장 */
        ChatMessage sysMsg = ChatMessage.systemMessage(chatRoom, enterText, ChatType.ENTER);
        chatMessageRepository.save(sysMsg);

        /* ③ 브로드캐스트  (/topic/{roomId}) */
        simpMessagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(),
                ChatMessageResponseDTO.from(sysMsg));
    }

    /** 2) 일반 대화 ── 참여자 확인 → ChatMessage INSERT → 브로드캐스트 */
    private void talk(ChatMessageDTO chatMessageDTO) {

        /* ① 상대가 방 안에 있는지 확인 (퇴장했으면 에러) */
        boolean receiverStillHere =
                chatRoomParticipantRepository.existsByChatRoom_RoomIdAndUser_UserId(chatMessageDTO.getRoomId(), chatMessageDTO.getReceiverId());
        if (!receiverStillHere) {
            throw new IllegalStateException("상대방이 이미 채팅방을 떠났습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId())
                .orElseThrow(()-> new IllegalArgumentException("방이 없습니다."));

        /* ② 메시지 저장 */
        ChatMessage chat = ChatMessage.userMessage(chatMessageDTO, chatRoom);
        chatMessageRepository.save(chat);

        /* ③ 브로드캐스트 */
        simpMessagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(),
                ChatMessageResponseDTO.from(chat));
    }

    /** 3) 퇴장 ── 참여자 DELETE + 시스템메시지 브로드캐스트 */
    private void leave(ChatMessageDTO chatMessageDTO) {

        /* ① 참여자 삭제 */
        chatRoomParticipantRepository.deleteByChatRoom_RoomIdAndUser_UserId(chatMessageDTO.getRoomId(), chatMessageDTO.getSenderId());

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방이 없습니다"));

        String leaveText = chatMessageDTO.getContent() + "님이 퇴장했습니다.";

        /* ② “OOO님이 퇴장했습니다” 시스템 메시지 */
        ChatMessage sysMsg = ChatMessage.systemMessage(
                chatRoom, leaveText, ChatType.LEAVE);
        chatMessageRepository.save(sysMsg);

        /* ③ 브로드캐스트 */
        simpMessagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(),
                ChatMessageResponseDTO.from(sysMsg));
    }
}
