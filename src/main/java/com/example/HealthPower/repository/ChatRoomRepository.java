package com.example.HealthPower.repository;

import com.example.HealthPower.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomId(Long roomId);

    ChatRoom getReferenceById(Long roomId);

    boolean existsByRoomId(Long roomId);

    /**
     * 특정 사용자가 아직 active 참가자가 아닌 모든 방
     *   - p.exited = false 까지 포함해 '정말 참여 중이 아닌' 방만
     */
    @Query("""
            select r from ChatRoom r
            where r.creatorId <> :userId
            and not exists (
                select p.id from ChatRoomParticipant p
                where p.chatRoom = r and p.user.userId = :userId
                and p.exited = false
                )
                order by r.updatedAt desc
            """)
    List<ChatRoom> findAllNotJoinedByUser(@Param("userId") String userId);

}
