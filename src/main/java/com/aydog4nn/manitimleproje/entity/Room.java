package com.aydog4nn.manitimleproje.entity;

import com.aydog4nn.manitimleproje.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {
    @Id @UuidGenerator private UUID id;
    @Column(nullable = false, length = 100) private String name;
    @Column(name = "invite_code", nullable = false, unique = true, length = 16) private String inviteCode;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_id") private User owner;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected Room() {}
    private Room(String name, String inviteCode, User owner) { this.name = name; this.inviteCode = inviteCode; this.owner = owner; }
    public static Room create(String name, String inviteCode, User owner) { return new Room(name, inviteCode, owner); }
    public void rename(String name) { this.name = name; }
    @PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getInviteCode() { return inviteCode; }
    public User getOwner() { return owner; }
    public Instant getCreatedAt() { return createdAt; }
}
