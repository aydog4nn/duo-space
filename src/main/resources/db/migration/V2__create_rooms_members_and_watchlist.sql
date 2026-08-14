CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    invite_code VARCHAR(16) NOT NULL,
    owner_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_rooms_invite_code UNIQUE (invite_code),
    CONSTRAINT fk_rooms_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE room_members (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_room_members_room_user UNIQUE (room_id, user_id),
    CONSTRAINT fk_room_members_room FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE,
    CONSTRAINT fk_room_members_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE watchlist_items (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL,
    added_by_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    source_url VARCHAR(2048),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_watchlist_items_room FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE,
    CONSTRAINT fk_watchlist_items_added_by FOREIGN KEY (added_by_id) REFERENCES users (id)
);
