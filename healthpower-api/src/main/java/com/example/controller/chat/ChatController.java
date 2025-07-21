package com.example.controller.chat;

import com.example.enumpackage.ChatType;
import com.example.dto.chat.ChatMessageDTO;
import com.example.dto.chat.ChatRoomDTO;
import com.example.dto.chat.ChatRoomListItemDTO;
import com.example.dto.chat.ChatUserDTO;
import com.example.entity.User;
import com.example.entity.chat.ChatMessage;
import com.example.entity.chat.ChatRoom;
import com.example.entity.chat.ChatRoomParticipant;
import com.example.impl.UserDetailsImpl;
import com.example.impl.UserDetailsServiceImpl;
import com.example.jwt.JwtTokenProvider;
import com.example.chatetc.PresenceStore;
import com.example.repository.ChatMessageRepository;
import com.example.repository.ChatRoomRepository;
import com.example.repository.UserRepository;
import com.example.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private final UserRepository userRepository;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final ChatService.FileStorageService fileStorage;
    private final PresenceStore store;

    @Value("${jwt.secret}")
    private String secret;

    @GetMapping("/create")
    public String createChatRoom(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "chat/createRoom";
    }

    @PostMapping("/create")
    public String createChatRoom(@AuthenticationPrincipal UserDetailsImpl user,
                                 @ModelAttribute ChatRoomDTO chatRoomDTO) {

        Long roomId = chatService.createRoom(chatRoomDTO.getName(), user.getUserId());

        return "redirect:/chat/chatRoom/" + roomId;
    }

    //로그인 유저의 채팅방 목록
    @GetMapping("/rooms")
    public String roomList(@RequestParam("userId") Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("사용자 없음"));

        model.addAttribute("rooms", user.getChatRooms());
        model.addAttribute("userId", user.getId());

        return "chat/chatList";
    }

    //메세지 내역 불러오기
    @GetMapping("/chatRoom/{roomId}")
    public String enterChatRoom(@PathVariable Long roomId,
                                HttpServletRequest request,
                                Authentication authentication,
                                @AuthenticationPrincipal UserDetailsImpl user,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        boolean allowed = chatService.hasUserActiveInRoom(roomId, user.getUserId());

        if (!allowed) {
            redirectAttributes.addFlashAttribute("msg", "이미 나간 채팅방입니다.");
            return "redirect:/chat/chatList";
        }

        String jwt = jwtTokenProvider.createToken(authentication);

        ChatRoom room = chatService.getChatRoomByRoomId(roomId);

        List<ChatMessage> chatHistory = chatService.getMessages(room);

        List<ChatRoomParticipant> users = chatService.getUsersByRoomId(roomId);

        model.addAttribute("jwt", jwt);
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", room.getName());
        model.addAttribute("users", users);
        model.addAttribute("messages", chatHistory);
        model.addAttribute("senderNickname", user.getNickname());
        model.addAttribute("currentUserId", user.getUserId());

        return "chat/chatRoom";
    }

    @MessageMapping("chatRoom/{roomId}")
    public void enter(@DestinationVariable Long roomId, Principal principal) {
        String userId = principal.getName();
        String nickname = String.valueOf(userDetailsService.loadUserByUsername(userId));
        store.addUser(roomId, new ChatUserDTO(userId, nickname));

        //채팅방에 참가자 명단 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId+"/users", store.getUsers(roomId));
    }

    //채팅방 목록
    @GetMapping("/chatList")
    @PreAuthorize("isAuthenticated()")
    public String myChatRooms(@AuthenticationPrincipal UserDetailsImpl user,
                              Model model) {

        // ① 내가 참가 중인 모든 방 (participant 기준)
        List<ChatRoomListItemDTO> chatRooms = chatService.getRoomsByUser(user.getUserId())
                .stream().sorted(Comparator.comparing(dto ->
                                dto.getChatRoom().getUpdatedAt(),
                        Comparator.reverseOrder()))
                .toList();

        List<ChatRoomListItemDTO> chatRoomOthers = chatService.getRoomsUserNotJoined(user.getUserId())
                .stream().sorted(Comparator.comparing(dto ->
                                dto.getChatRoom().getUpdatedAt(),
                        Comparator.reverseOrder()))
                .toList();

        // ② 내가 만든 방
        List<ChatRoomListItemDTO> myRooms = chatRooms.stream()
                .filter(r -> r.getChatRoom().getCreatorId().equals(user.getUserId())).toList();

        // 다른 사람이 만든 방 분리
        List<ChatRoomListItemDTO> otherRooms = chatRoomOthers.stream()
                .filter(r -> !r.getChatRoom().getCreatorId().equals(user.getUserId())).toList();

        List<User> userList = userRepository.findAll()
                .stream()
                .filter(u -> !u.getUserId().equals(user.getUserId()))
                .toList();

        model.addAttribute("chatRooms", chatRooms);
        model.addAttribute("myRooms", myRooms);
        model.addAttribute("otherRooms", otherRooms);
        model.addAttribute("currentUserId", user.getUserId());

        return "chat/chatList";
    }

    //채팅방 나가기
    @PostMapping("/exit/{roomId}")
    public String exitRoom(@AuthenticationPrincipal UserDetailsImpl user,
                           @PathVariable Long roomId) {
        ChatRoom chatRoom = chatService.getChatRoomByRoomId(roomId);
        chatService.markUserExited(chatRoom, user.getUserId());

        return "redirect:/chat/chatList";
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDTO chatMessageDTO,
                            Principal principal) {

        // ① 인증 유저 → senderId 주입
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        ChatMessage chatMessage = ChatMessage.userMessage(chatMessageDTO, chatRoom);


        String senderId = principal.getName();

        User user = userRepository.findByUserId(senderId).orElse(null);

        chatMessageDTO.setSenderId(senderId);
        chatMessageDTO.setTimeStamp(chatMessage.getSentAt());
        chatMessageDTO.setImageUrl(user.getImageUrl());

        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(), chatMessageDTO);

        // ② roomId 누락 방지 – 클라이언트가 안 줬다면 URL/Session 로부터
        if (chatMessageDTO.getRoomId() == null) {
            Long roomId = (Long) SimpAttributesContextHolder.currentAttributes()
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
        chatMessageDTO.setChatType(ChatType.EXIT);
        chatService.handle(chatMessageDTO);
    }

    @PostMapping(value = "/upload", consumes= "multipart/form-data")
    public ResponseEntity<Void> upload(ChatMessageDTO chatMessageDTO, @RequestPart("file") MultipartFile file) throws IOException {

        String url = fileStorage.save(chatMessageDTO.getRoomId(), file);

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(chatMessageDTO.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방이 존재하지 않습니다."));

        ChatMessage msg = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(chatMessageDTO.getSenderId())
                .senderNickname(chatMessageDTO.getSenderNickname())
                .chatType(ChatType.IMAGE)
                .content(url)
                .sentAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(msg);

        messagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(), of(msg));

        return ResponseEntity.ok().build();
    }

    private Object of(ChatMessage msg) {
        return msg;
    }

    public ChatMessage toEntity(ChatMessageDTO chatMessageDTO) {
        return ChatMessage.builder()
                .senderId(chatMessageDTO.getSenderId())
                .content(chatMessageDTO.getContent())
                .chatType(chatMessageDTO.getChatType())
                .build();
    }

}
