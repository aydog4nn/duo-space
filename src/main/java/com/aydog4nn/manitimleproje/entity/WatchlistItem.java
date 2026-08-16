package com.aydog4nn.manitimleproje.entity;

import com.aydog4nn.manitimleproje.entity.Room;
import com.aydog4nn.manitimleproje.entity.User;
import com.aydog4nn.manitimleproje.entity.enums.WatchlistStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "watchlist_items")
public class WatchlistItem {
    @Id @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "room_id") private Room room;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "added_by_id") private User addedBy;
    @Column(nullable = false) private String title;
    @Column(name = "source_url", length = 2048) private String sourceUrl;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private WatchlistStatus status;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    protected WatchlistItem() {}
    private WatchlistItem(Room room, User addedBy, String title, String sourceUrl) { this.room = room; this.addedBy = addedBy; this.title = title; this.sourceUrl = sourceUrl; status = WatchlistStatus.PLANNED; }
    public static WatchlistItem create(Room room, User addedBy, String title, String sourceUrl) { return new WatchlistItem(room, addedBy, title, sourceUrl); }
    public void update(String title, String sourceUrl, WatchlistStatus status) { this.title = title; this.sourceUrl = sourceUrl; this.status = status; }
    @PrePersist void onCreate() { createdAt = Instant.now(); }
    public UUID getId() { return id; }
    public Room getRoom() { return room; }
    public String getTitle() { return title; }
    public String getSourceUrl() { return sourceUrl; }
    public WatchlistStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
