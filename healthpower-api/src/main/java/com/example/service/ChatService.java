package com.example.service;

import com.example.enumpackage.ChatType;
import com.example.dto.chat.ChatMessageDTO;
import com.example.dto.chat.ChatMessageResponseDTO;
import com.example.dto.chat.ChatRoomListItemDTO;
import com.example.entity.User;
import com.example.entity.chat.ChatMessage;
import com.example.entity.chat.ChatRoom;
import com.example.entity.chat.ChatRoomParticipant;
import com.example.exception.user.UserNotFoundException;
import com.example.repository.ChatMessageRepository;
import com.example.repository.ChatRoomParticipantRepository;
import com.example.repository.ChatRoomRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final UserRepository userRepository;


    @Transactional
    public Long createRoom(String name, String creatorId) {
        User creator = userRepository.findByUserId(creatorId)
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoom.setCreatorId(creatorId);
        chatRoomRepository.save(chatRoom);

        ChatRoomParticipant cp = ChatRoomParticipant.of(chatRoom, creator, true);

        chatRoomParticipantRepository.save(cp);

        return chatRoom.getRoomId();
    }

    // 두 사용자 간의 고정된 채팅방 생성 or 조회
    @Transactional
    public ChatRoom createRoomId(String userA, String userB) {

        if (userA.equals(userB)) {
            throw new IllegalStateException(("자신과 채팅할 수 없습니다."));
        }

        /* 순서 고정 */
        String a = userA.compareTo(userB) < 0 ? userA : userB;
        String b = userA.compareTo(userB) < 0 ? userB : userA;
        Long roomId = Long.valueOf(a + "_" + b);

        ChatRoom room = chatRoomRepository.findByRoomId(roomId).orElse(null);
        if (room != null) {
            verifyNotExited(roomId, userA, userB);
            return room;
        }

        if (chatRoomParticipantRepository.existsByChatRoomRoomId(roomId)) {
            throw new IllegalStateException("이미 존재했던 채팅방입니다. 입장할 수 없습니다.");
        }

        room = chatRoomRepository.save(new ChatRoom());

        User A = userRepository.findByUserId(userA).orElseThrow();
        User B = userRepository.findByUserId(userB).orElseThrow();

        chatRoomParticipantRepository.save(new ChatRoomParticipant(room, A));
        chatRoomParticipantRepository.save(new ChatRoomParticipant(room, B));

        return room;
    }

    public List<ChatRoomListItemDTO> getRoomsUserNotJoined(String userId) {
        return chatRoomRepository.findAllNotJoinedByUser(userId)
                .stream()
                .map(r -> new ChatRoomListItemDTO(r, false)) // exited 의미X
                .toList();
    }

    private void verifyNotExited(Long roomId, String userA, String userB) {
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
                ChatMessageDTO.getSenderId(),
                ChatMessageDTO.getContent(),
                ChatMessageDTO.getTimeStamp(),
                chatRoom
        );

        chatMessageRepository.save(message);

        System.out.println("띠리링");
    }

    // 사용자가 속한 모든 채팅방 조회
    public List<ChatRoomListItemDTO> getRoomsByUser(String userId) {

        List<ChatRoomParticipant> participants = chatRoomParticipantRepository.findByUserUserIdAndExitedFalse(userId);

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

        ChatRoomParticipant participant = chatRoomParticipantRepository.findByChatRoomAndUser(chatRoom, user)
                .orElseThrow();

        if (participant.isOwner()) {
            chatMessageRepository.deleteByChatRoom(chatRoom);
            chatRoomParticipantRepository.deleteByChatRoom(chatRoom);
            chatRoomRepository.delete(chatRoom);
            return;
        }

        participant.setExited(true);
    }

    public boolean hasUserActiveInRoom(Long roomId, String userId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("방이 존재하지 않습니다"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        /*return chatRoomchatRoomParticipantRepositorysitory.findByChatRoomAndUser(room, user)
                .filter(participant -> !participant.isExited()) // 나간 상태가 아니어야 입장 허용
                .isPresent();*/
        return chatRoomParticipantRepository.findByChatRoomRoomIdAndUserUserId(roomId, userId)
                .map(p -> !p.isExited())
                .orElse(true);
    }

    public ChatRoom getChatRoomByRoomId(Long roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다: " + roomId));
    }

    public void handle(ChatMessageDTO chatMessageDTO) {

        ChatRoom room = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방이 없습니다."));

        switch (chatMessageDTO.getChatType()) {
            case ENTER -> enter(chatMessageDTO);
            case TALK -> talk(chatMessageDTO);
            case EXIT -> leave(chatMessageDTO);
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

        String senderId = chatMessageDTO.getSenderId();

        String enterText = senderId + "님이 입장했습니다";

        /* ② “OOO님이 입장했습니다” 시스템 메시지 저장 */
        ChatMessage sysMsg = ChatMessage.systemMessage(chatRoom, enterText, senderId, ChatType.ENTER);
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

        String senderId = chatMessageDTO.getSenderId();

        String leaveText = senderId + "님이 퇴장했습니다.";

        /* ② “OOO님이 퇴장했습니다” 시스템 메시지 */
        ChatMessage sysMsg = ChatMessage.systemMessage(
                chatRoom, leaveText, senderId, ChatType.EXIT);
        chatMessageRepository.save(sysMsg);

        /* ③ 브로드캐스트 */
        simpMessagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(),
                ChatMessageResponseDTO.from(sysMsg));
    }

    public List<ChatRoomParticipant> getUsersByRoomId(Long roomId) {
        return chatRoomParticipantRepository.findByChatRoom_RoomIdAndExitedFalse(roomId);
    }

    @Service
    public class FileStorageService {

        @Value("${app.upload.dir}")
        private String rootDir;

        public String save(Long roomId, MultipartFile file) throws IOException {

            // 1) 디렉터리 없으면 생성
            Path roomPath = Paths.get(rootDir, String.valueOf(roomId));
            Files.createDirectories(roomPath);

            // 2) 파일명 생성
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + "." + ext;

            // 3) 복사
            Path target = roomPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // 4) 정적 리소스 URL 반환
            return "/uploads/" + roomId + "/" + filename;     // 실제로는 CDN·S3 URL 등
        }
    }

}
