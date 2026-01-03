CREATE TABLE notification (
    id BINARY(16) NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    recipient_id BINARY(16) NOT NULL,
    actor_id BINARY(16) NOT NULL,
    target_type VARCHAR(255) NOT NULL,
    target_id BINARY(16),
    is_read BIT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

ALTER TABLE notification
    ADD CONSTRAINT FK_notification_actor
    FOREIGN KEY (actor_id)
    REFERENCES users (id);

ALTER TABLE notification
    ADD CONSTRAINT FK_notification_recipient
    FOREIGN KEY (recipient_id)
    REFERENCES users (id);

CREATE INDEX idx_notification_recipient_id ON notification(recipient_id);
CREATE INDEX idx_notification_recipient_id_is_read ON notification(recipient_id, is_read);
