ALTER TABLE users ADD COLUMN role VARCHAR(255);

UPDATE users u
JOIN user_auths ua ON u.id = ua.user_id
SET u.role = 'STUDENT'
WHERE ua.provider = 'M365' AND ua.email LIKE '%edu.vn';

UPDATE users SET role = 'USER' WHERE role IS NULL;

ALTER TABLE users MODIFY COLUMN role VARCHAR(255) NOT NULL;
