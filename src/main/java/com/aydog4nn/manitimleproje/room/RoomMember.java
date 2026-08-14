package com.aydog4nn.manitimleproje.room;

import com.aydog4nn.manitimleproje.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "room_members")
public class RoomMember {
    @Id @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "room_id") private Room room;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private RoomMemberRole role;
    @Column(name = "joined_at", nullable = false, updatable = false) private Instant joinedAt;
    protected RoomMember() {}
    private RoomMember(Room room, User user, RoomMemberRole role) { this.room = room; this.user = user; this.role = role; }
    public static RoomMember owner(Room room, User user) { return new RoomMember(room, user, RoomMemberRole.OWNER); }
    @PrePersist void onCreate() { joinedAt = Instant.now(); }
}
