package com.example.HealthPower.chatetc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class DisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private final PresenceStore store;
    private final SimpMessagingTemplate template;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(event.getMessage());
        Principal p = acc.getUser();            // userId
        String roomHeader = acc.getFirstNativeHeader("roomId");

        if (p != null && roomHeader != null){
            Long roomId = Long.valueOf(roomHeader);
            store.removeUser(roomId, p.getName());
            template.convertAndSend("/topic/room/"+roomId+"/users", store.getUsers(roomId));
        }
    }
}
