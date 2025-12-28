CREATE TABLE conversation
(
    id         BINARY(16)                        NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    title      VARCHAR(255)                      NOT NULL,
    type       ENUM ('PRIVATE', 'GROUP')         NOT NULL,
    created_by BINARY(16)                        NOT NULL,
    CONSTRAINT pk_conversation PRIMARY KEY (id),
    CONSTRAINT fk_conversation_created_by FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE INDEX idx_title ON conversation (title, type);

CREATE TABLE conversation_member
(
    id              BINARY(16)               NOT NULL,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    joined_at       DATETIME(6)              NOT NULL,
    role            ENUM ('ADMIN', 'MEMBER') NOT NULL,
    conversation_id BINARY(16)               NOT NULL,
    member_id       BINARY(16)               NOT NULL,
    CONSTRAINT pk_conversation_member PRIMARY KEY (id),
    CONSTRAINT fk_conversation_member_conversation FOREIGN KEY (conversation_id) REFERENCES conversation (id),
    CONSTRAINT fk_conversation_member_member FOREIGN KEY (member_id) REFERENCES users (id)
);

CREATE INDEX idx_conversation_member_conversation_id ON conversation_member (conversation_id);
CREATE INDEX idx_conversation_member_member_id ON conversation_member (member_id);

CREATE TABLE message
(
    id              BINARY(16)            NOT NULL,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    content         TEXT                  NOT NULL,
    type            ENUM ('USER', 'SYSTEM') NOT NULL,
    conversation_id BINARY(16)            NOT NULL,
    sender_id       BINARY(16)            NOT NULL,
    CONSTRAINT pk_message PRIMARY KEY (id),
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversation (id),
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users (id)
);

CREATE INDEX idx_message_conversation_id ON message (conversation_id);
CREATE INDEX idx_message_content ON message (content(255));

CREATE TABLE message_read
(
    id         BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    read_at    DATETIME(6) NOT NULL,
    message_id BINARY(16) NOT NULL,
    reader_id  BINARY(16) NOT NULL,
    CONSTRAINT pk_message_read PRIMARY KEY (id),
    CONSTRAINT fk_message_read_message FOREIGN KEY (message_id) REFERENCES message (id),
    CONSTRAINT fk_message_read_reader FOREIGN KEY (reader_id) REFERENCES users (id)
);
