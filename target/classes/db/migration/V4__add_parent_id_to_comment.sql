ALTER TABLE comment
    ADD COLUMN parent_id BINARY(16) NULL,
    ADD CONSTRAINT fk_comment_parent
        FOREIGN KEY (parent_id) REFERENCES comment(id);
