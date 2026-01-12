ALTER TABLE conversation ADD COLUMN avatar_key VARCHAR(255) DEFAULT '/conversation-avatars/default.png';
UPDATE conversation SET avatar_key = '/conversation-avatars/default.png' WHERE avatar_key IS NULL;
