DROP PROCEDURE IF EXISTS MigrateUnifyMedia;

DELIMITER //

CREATE PROCEDURE MigrateUnifyMedia()
BEGIN
    -- Check if media table exists
    IF NOT EXISTS (SELECT * FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'media') THEN
        CREATE TABLE media
        (
            media_id    BINARY(16)   NOT NULL,
            target_id   BINARY(16)   NOT NULL,
            target_type VARCHAR(20)  NOT NULL,
            type        VARCHAR(20)  NOT NULL,
            object_key  VARCHAR(255) NOT NULL,
            order_index INT          NOT NULL,
            CONSTRAINT pk_media PRIMARY KEY (media_id)
        );
        CREATE INDEX idx_media_target ON media (target_id, target_type);
    END IF;

    -- Migrate PostMedia
    IF EXISTS (SELECT * FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'post_media') THEN
        INSERT IGNORE INTO media (media_id, target_id, target_type, type, object_key, order_index)
        SELECT UUID_TO_BIN(UUID()), post_id, 'POST', type, object_key, order_index
        FROM post_media;
        
        DROP TABLE post_media;
    END IF;

    -- Migrate CommentMedia
    IF EXISTS (SELECT * FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'comment_media') THEN
        INSERT IGNORE INTO media (media_id, target_id, target_type, type, object_key, order_index)
        SELECT UUID_TO_BIN(UUID()), comment_id, 'COMMENT', type, object_key, order_index
        FROM comment_media;
        
        DROP TABLE comment_media;
    END IF;

END //

DELIMITER ;

CALL MigrateUnifyMedia();

DROP PROCEDURE MigrateUnifyMedia;
