package com.example.HealthPower.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.chat.ChatRoom;
import com.example.HealthPower.entity.chat.ChatRoomParticipant;
import com.example.HealthPower.entity.chat.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, String> {

    /** 기존 기록(soft-delete 포함 여부)을 조회 */
    boolean existsByChatRoomRoomId(Long roomId);

    Optional<ChatRoomParticipant> findByChatRoomRoomIdAndUserUserId(Long roomId, String userId);

    //특정 방에서 특정 유저의 참여 정보 조회
    Optional<ChatRoomParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    //해당 채팅방에 참여한 모든 유저 조회
    List<ChatRoomParticipant> findByChatRoom(ChatRoom chatRoom);

    //나가지 않은 방만 목록에서 보여줄 때 사용
    List<ChatRoomParticipant> findByUserUserIdAndExitedFalse(String userId);

    List<ChatRoomParticipant> findByUser_UserId(String userId);

    //방 완전 삭제 시 관련 participant 정보 같이 삭제
    void deleteByChatRoom(ChatRoom chatRoom);

    @Modifying
    @Transactional
    void deleteByChatRoom_RoomIdAndUser_UserId(Long roomId, String userId);

    boolean existsByChatRoom_RoomIdAndUser_UserId(Long roomId, String userId);

    List<ChatRoomParticipant> findByChatRoom_RoomIdAndExitedFalse(Long roomId);
}
