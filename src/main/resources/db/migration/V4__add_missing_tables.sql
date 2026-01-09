-- 1. Club
CREATE TABLE club (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    avatar_key VARCHAR(255),
    background_key VARCHAR(255),
    description TEXT,
    follower_count INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 2. Club Follower
CREATE TABLE club_follower (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    club_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_club_follower_club_user UNIQUE (club_id, user_id),
    CONSTRAINT FK_club_follower_club_id FOREIGN KEY (club_id) REFERENCES club (id),
    CONSTRAINT FK_club_follower_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 3. Club Moderator
CREATE TABLE club_moderator (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    club_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_club_moderator_club_user UNIQUE (club_id, user_id),
    CONSTRAINT FK_club_moderator_club_id FOREIGN KEY (club_id) REFERENCES club (id),
    CONSTRAINT FK_club_moderator_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 4. Event
CREATE TABLE event (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    banner_key VARCHAR(255),
    description TEXT,
    end_time DATETIME(6) NOT NULL,
    location VARCHAR(255),
    max_participants INTEGER NOT NULL,
    registered_count INTEGER NOT NULL,
    start_time DATETIME(6) NOT NULL,
    status VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    club_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_event_club_id FOREIGN KEY (club_id) REFERENCES club (id)
) ENGINE=InnoDB;

-- 5. Event Participant
CREATE TABLE event_participant (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    registered_at DATETIME(6) NOT NULL,
    status VARCHAR(255) NOT NULL,
    event_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_event_participant_event_user UNIQUE (event_id, user_id),
    CONSTRAINT FK_event_participant_event_id FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT FK_event_participant_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;
