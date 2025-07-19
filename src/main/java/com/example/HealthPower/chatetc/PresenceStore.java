package com.example.HealthPower.chatetc;

import com.example.HealthPower.dto.chat.ChatUserDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceStore {
    // 여러 스레드가 건드리므로 ConcurrentHashMap 사용

    private final Map<Long, Set<ChatUserDTO>> rooms = new ConcurrentHashMap<>();

    public void addUser(Long roomId, ChatUserDTO chatUserDTO) {
        rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(chatUserDTO);
    }

    public void removeUser(Long roomId, String userId) {
        rooms.computeIfPresent(roomId, (k, set) -> {
            set.removeIf(s -> s.userId().equals(userId));
            return set.isEmpty() ? null : set;
        });
    }

    public Set<ChatUserDTO> getUsers(Long roomId) {
        return rooms.getOrDefault(roomId, Set.of());
    }

}
