package com.aydog4nn.manitimleproje.repository;

import com.aydog4nn.manitimleproje.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByOwner_Id(UUID ownerId);
}
