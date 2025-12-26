-- 1. Tạo bảng mới
CREATE TABLE likes (
                       id BINARY(16) PRIMARY KEY,
                       user_id BINARY(16) NOT NULL,
                       post_id BINARY(16),
                       comment_id BINARY(16),
                       target_type VARCHAR(20) NOT NULL,
                       created_at DATETIME,

                       CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id),
                       CONSTRAINT fk_likes_post FOREIGN KEY (post_id) REFERENCES post(post_id),
                       CONSTRAINT fk_likes_comment FOREIGN KEY (comment_id) REFERENCES comment(id)
);

-- 2. Migrate dữ liệu từ post_like
INSERT INTO likes (id, user_id, post_id, target_type, created_at)
SELECT
    id,
    user_id,
    post_id,
    'POST',
    NOW()
FROM post_like;

-- 3. Migrate dữ liệu từ comment_like
INSERT INTO likes (id, user_id, comment_id, target_type, created_at)
SELECT
    id,
    user_id,
    comment_id,
    'COMMENT',
    NOW()
FROM comment_like;

-- 4. Drop bảng cũ
DROP TABLE post_like;
DROP TABLE comment_like;
