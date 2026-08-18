package com.aydog4nn.manitimleproje.repository;

import com.aydog4nn.manitimleproje.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByOwner_Id(UUID ownerId);

    Optional<Room> findByInviteCode(String inviteCode);

    @Query("select room from Room room join RoomMember member on member.room = room where member.user.id = :userId")
    List<Room> findAccessibleByUserId(@Param("userId") UUID userId);
}
