package com.example.HealthPower.dto.chat;

import com.example.HealthPower.entity.chat.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomListItemDTO {
    private ChatRoom chatRoom;
    private boolean exited;

    public ChatRoomListItemDTO(ChatRoom chatRoom, boolean exited) {
        this.chatRoom = chatRoom;
        this.exited = exited;
    }
}
