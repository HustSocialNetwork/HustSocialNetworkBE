UPDATE likes SET target_id = post_id WHERE post_id IS NOT NULL;
UPDATE likes SET target_id = comment_id WHERE comment_id IS NOT NULL;

ALTER TABLE likes DROP COLUMN post_id;
ALTER TABLE likes DROP COLUMN comment_id;
