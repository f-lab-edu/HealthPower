package com.example.dto.chat;

import com.example.entity.chat.ChatRoom;
import lombok.Getter;

@Getter
public class ChatRoomListItemDTO {
    private ChatRoom chatRoom;
    private boolean exited;

    public ChatRoomListItemDTO(ChatRoom chatRoom, boolean exited) {
        this.chatRoom = chatRoom;
        this.exited = exited;
    }
}
