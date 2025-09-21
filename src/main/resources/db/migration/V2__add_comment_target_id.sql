ALTER TABLE comments
ADD COLUMN target_id CHAR(36) NULL;

ALTER TABLE comments
ADD CONSTRAINT fk_comment_target
FOREIGN KEY (target_id) REFERENCES comments(id)
ON DELETE CASCADE;
