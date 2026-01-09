-- 1. Users
CREATE TABLE users (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    avatar_key VARCHAR(255),
    background_key VARCHAR(255),
    bio VARCHAR(200),
    display_name VARCHAR(255) NOT NULL,
    email_verified BIT,
    first_name VARCHAR(255),
    follower_count INTEGER,
    following INTEGER,
    friends INTEGER,
    last_name VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 2. User Auths
CREATE TABLE user_auths (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    provider VARCHAR(255) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_user_auths_provider_email UNIQUE (provider, email),
    CONSTRAINT FK_user_auths_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 3. Post
CREATE TABLE post (
    post_id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    comments_count INTEGER,
    content TEXT NOT NULL,
    likes_count INTEGER,
    status VARCHAR(255) NOT NULL,
    visibility VARCHAR(255) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (post_id),
    CONSTRAINT FK_post_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 4. Comment
CREATE TABLE comment (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    content TEXT NOT NULL,
    likes_count INTEGER NOT NULL,
    replies_count INTEGER NOT NULL,
    parent_id BINARY(16),
    post_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_comment_parent_id FOREIGN KEY (parent_id) REFERENCES comment (id),
    CONSTRAINT FK_comment_post_id FOREIGN KEY (post_id) REFERENCES post (post_id),
    CONSTRAINT FK_comment_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 5. Media
CREATE TABLE media (
    media_id BINARY(16) NOT NULL,
    object_key VARCHAR(255) NOT NULL,
    order_index INTEGER NOT NULL,
    target_id BINARY(16) NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    PRIMARY KEY (media_id),
    INDEX idx_media_target (target_id, target_type)
) ENGINE=InnoDB;

-- 6. Friendship
CREATE TABLE friendship (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    status VARCHAR(255),
    receiver_id BINARY(16) NOT NULL,
    requester_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_friendship_requester_receiver UNIQUE (requester_id, receiver_id),
    CONSTRAINT FK_friendship_receiver_id FOREIGN KEY (receiver_id) REFERENCES users (id),
    CONSTRAINT FK_friendship_requester_id FOREIGN KEY (requester_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 7. Block
CREATE TABLE block (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    blocked_id BINARY(16) NOT NULL,
    blocker_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_block_blocker_blocked UNIQUE (blocker_id, blocked_id),
    CONSTRAINT FK_block_blocked_id FOREIGN KEY (blocked_id) REFERENCES users (id),
    CONSTRAINT FK_block_blocker_id FOREIGN KEY (blocker_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 8. Likes
CREATE TABLE likes (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    target_id BINARY(16) NOT NULL,
    target_type VARCHAR(255) NOT NULL,
    user_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UK_likes_user_target UNIQUE (user_id, target_id, target_type),
    CONSTRAINT FK_likes_user_id FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 9. Conversation
CREATE TABLE conversation (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    title VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_by BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_title (title, type),
    CONSTRAINT FK_conversation_created_by FOREIGN KEY (created_by) REFERENCES users (id)
) ENGINE=InnoDB;

-- 10. Conversation Member
CREATE TABLE conversation_member (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    joined_at DATETIME(6) NOT NULL,
    role VARCHAR(20) NOT NULL,
    conversation_id BINARY(16) NOT NULL,
    member_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_conversation_member_conversation_id (conversation_id),
    INDEX idx_conversation_member_member_id (member_id),
    CONSTRAINT FK_conversation_member_conversation_id FOREIGN KEY (conversation_id) REFERENCES conversation (id),
    CONSTRAINT FK_conversation_member_member_id FOREIGN KEY (member_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 11. Message
CREATE TABLE message (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    content TEXT NOT NULL,
    type VARCHAR(20) NOT NULL,
    conversation_id BINARY(16) NOT NULL,
    sender_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_message_conversation_id (conversation_id),
    INDEX idx_message_content (content(255)),
    CONSTRAINT FK_message_conversation_id FOREIGN KEY (conversation_id) REFERENCES conversation (id),
    CONSTRAINT FK_message_sender_id FOREIGN KEY (sender_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 12. Message Read
CREATE TABLE message_read (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    read_at DATETIME(6) NOT NULL,
    message_id BINARY(16) NOT NULL,
    reader_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_message_read_message_id FOREIGN KEY (message_id) REFERENCES message (id),
    CONSTRAINT FK_message_read_reader_id FOREIGN KEY (reader_id) REFERENCES users (id)
) ENGINE=InnoDB;
