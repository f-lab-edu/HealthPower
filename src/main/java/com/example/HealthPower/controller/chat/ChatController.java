package com.example.HealthPower.controller.chat;

import com.example.HealthPower.chatType.ChatType;
import com.example.HealthPower.dto.chat.ChatMessageDTO;
import com.example.HealthPower.dto.chat.ChatRoomListItemDTO;
import com.example.HealthPower.dto.user.UserDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.chat.ChatMessage;
import com.example.HealthPower.entity.chat.ChatRoom;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.jwt.JwtTokenProvider;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.ChatService;
import com.example.HealthPower.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private final UserRepository userRepository;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/enter/{targetUserId}")
    public String enterChatRoom(@AuthenticationPrincipal UserDetailsImpl user,
                                @PathVariable("targetUserId") String targetUserId,
                                RedirectAttributes redirectAttributes) {
        try {
            ChatRoom chatRoom = chatService.createRoomId(user.getUserId(), targetUserId);
            return "redirect:/chat/" + chatRoom.getRoomId();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/chat/list"; // 에러 메시지와 함께 목록으로 리디렉션
        }
    }

    /*@MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO,
                            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {

        String userId = (String) sessionAttributes.get("userId");

        User sender = chatService.getByUserId(userId);

        ChatRoom chatRoom = chatService.getChatRoomByRoomId(chatMessageDTO.getRoomId());

        ChatMessageDTO enrichedMessage = ChatMessageDTO.builder()
                .roomId(chatMessageDTO.getRoomId())
                .senderId(sender.getUserId())
                .senderNickname(sender.getNickname())
                .photoUrl(sender.getPhotoUrl())
                .receiverId(chatMessageDTO.getReceiverId())
                .content(chatMessageDTO.getContent())
                .timeStamp(java.time.LocalDateTime.now())
                .build();

        chatService.save(chatRoom, enrichedMessage);
        messagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(), enrichedMessage);

        System.out.println("토로로롱");
    }*/

    //메세지 내역 불러오기
    @GetMapping("/{roomId}")
    public String enterChatRoom(@PathVariable String roomId,
                                HttpServletRequest request,
                                @AuthenticationPrincipal UserDetailsImpl user,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        boolean allowed = chatService.hasUserActiveInRoom(roomId, user.getUserId());

        if (!allowed) {
            redirectAttributes.addFlashAttribute("msg", "이미 나간 채팅방입니다.");
            return "redirect:/chat/list";
        }

        ChatRoom room = chatService.getChatRoomByRoomId(roomId);

        /*List<ChatMessage> chatHistory = chatService.getMessages(roomId);*/
        List<ChatMessage> chatHistory = chatService.getMessages(room);

        model.addAttribute("roomId", roomId);
        model.addAttribute("currentUserId", user.getUserId());
        model.addAttribute("jwt", request.getHeader("Authorization"));

        // roomId로 상대방 ID 추출 (user1_user2 구조 활용)
        /*String[] ids = roomId.split("_");
        String receiverId = ids[0].equals(user.getUserId()) ? ids[1] : ids[0];
        model.addAttribute("receiverId", receiverId);*/

        String receiverId = room.getParticipantA().equals(user.getUserId())
                ? room.getParticipantB() : room.getParticipantA();

        //상대방 정보 가져오기
        User partner = chatService.getByUserId(receiverId);
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("partnerName", partner.getUsername());
        model.addAttribute("partnerPhoto", partner.getPhotoUrl());

        model.addAttribute("messages", chatHistory);

        return "chatRoom";
    }

    //채팅방 목록
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String myChatRooms(@AuthenticationPrincipal UserDetailsImpl user,
                              Model model) {
        List<ChatRoomListItemDTO> chatRooms = chatService.getRoomsByUser(user.getUserId())
                .stream().sorted(Comparator.comparing(dto ->
                                dto.getChatRoom().getUpdatedAt(),
                        Comparator.reverseOrder()))
                .toList();

        List<User> userList = userRepository.findAll()
                .stream()
                .filter(u -> !u.getUserId().equals(user.getUserId()))
                .toList();

        model.addAttribute("chatrooms", chatRooms);
        model.addAttribute("currentUserId", user.getUserId());
        model.addAttribute("userList", userList);
        return "chatList";
    }

    //채팅방 나가기
    @PostMapping("/exit/{roomId}")
    public String exitRoom(@AuthenticationPrincipal UserDetailsImpl user,
                           @PathVariable String roomId) {
        log.info("채팅방 나가기 요청: roomId={}, userId={}", roomId, user.getUserId());

        ChatRoom chatRoom = chatService.getChatRoomByRoomId(roomId);
        chatService.markUserExited(chatRoom, user.getUserId());

        return "redirect:/chat/list";
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDTO chatMessageDTO,
                            Principal principal) {

        // ① 인증 유저 → senderId 주입
        String senderId = principal.getName();
        chatMessageDTO.setSenderId(senderId);

        // ② roomId 누락 방지 – 클라이언트가 안 줬다면 URL/Session 로부터
        if (chatMessageDTO.getRoomId() == null) {
            String roomId = (String) SimpAttributesContextHolder.currentAttributes()
                    .getAttribute("roomId");
            chatMessageDTO.setRoomId(roomId);
        }

        chatMessageDTO.setChatType(ChatType.TALK);
        // 프론트에서 TALK 로 넘어오므로 그대로 처리
        chatService.handle(chatMessageDTO);
    }

    @MessageMapping("/chat.enter")
    public void enterRoom(ChatMessageDTO chatMessageDTO) {
        chatMessageDTO.setChatType(ChatType.ENTER);
        chatService.handle(chatMessageDTO);
    }

    @MessageMapping("/chat.exit")
    public void exitRoom(ChatMessageDTO chatMessageDTO) {
        chatMessageDTO.setChatType(ChatType.LEAVE);
        chatService.handle(chatMessageDTO);
    }

    public ChatMessage toEntity(ChatMessageDTO chatMessageDTO) {
        return ChatMessage.builder()
                .roomId(chatMessageDTO.getRoomId())
                .senderId(chatMessageDTO.getSenderId())
                .receiverId(chatMessageDTO.getReceiverId())
                .content(chatMessageDTO.getContent())
                .chatType(chatMessageDTO.getChatType())
                .build();
    }

}
