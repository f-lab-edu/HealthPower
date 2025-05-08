package com.example.HealthPower.controller.chat;

import com.example.HealthPower.dto.chat.ChatMessageDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.chat.ChatMessage;
import com.example.HealthPower.entity.chat.ChatRoom;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.UserRepository;
import com.example.HealthPower.service.ChatService;
import com.example.HealthPower.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/enter/{targetUserId}")
    public String enterChatRoom(@AuthenticationPrincipal UserDetailsImpl user,
                                @PathVariable("targetUserId") String targetUserId,
                                RedirectAttributes redirectAttributes) {
        ChatRoom chatRoom = chatService.createRoomId(user.getUserId(), targetUserId);
        return "redirect:/chat/" + chatRoom.getRoomId();
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        chatService.save(chatMessageDTO);
        messagingTemplate.convertAndSend("/topic/" + chatMessageDTO.getRoomId(), chatMessageDTO);
    }

    //메세지 내역 불러오기
    @GetMapping("/{roomId}")
    public String enterChatRoom(@PathVariable String roomId,
                                @AuthenticationPrincipal UserDetailsImpl user,
                                Model model) {
        List<ChatMessage> chatHistory = chatService.getMessages(roomId);
        model.addAttribute("roomId", roomId);
        model.addAttribute("currentUserId", user.getUserId());

        // roomId로 상대방 ID 추출 (user1_user2 구조 활용)
        String[] ids = roomId.split("_");
        String receiverId = ids[0].equals(user.getUserId()) ? ids[1] : ids[0];
        model.addAttribute("receiverId", receiverId);

        //상대방 정보 가져오기
        User partner = chatService.getByUserId(receiverId);
        model.addAttribute("partnerName", partner.getUsername());
        model.addAttribute("partnerPhoto", partner.getPhotoPath()); //파일명이 저장되어 있다고 가정

        model.addAttribute("messages", chatHistory);

        return "chatRoom";
    }

    //채팅방 목록
    @GetMapping("/list")
    public String myChatRooms(@AuthenticationPrincipal UserDetailsImpl user,
                              Model model) {
        List<ChatRoom> chatRooms = chatService.getRoomsByUser(user.getUserId());
        model.addAttribute("chatrooms", chatRooms);
        model.addAttribute("currentUserId", user.getUserId());
        return "chatList";
    }

}
