package com.aydog4nn.manitimleproje.room;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
}
