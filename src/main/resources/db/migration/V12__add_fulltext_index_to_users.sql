ALTER TABLE users ADD FULLTEXT INDEX idx_fulltext_user_name (display_name, first_name, last_name);
