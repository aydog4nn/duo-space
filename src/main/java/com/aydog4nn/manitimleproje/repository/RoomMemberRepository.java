package com.aydog4nn.manitimleproje.repository;

import com.aydog4nn.manitimleproje.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
}
