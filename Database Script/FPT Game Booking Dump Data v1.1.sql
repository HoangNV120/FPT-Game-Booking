-- User 1: Regular user
INSERT INTO users (name, email, password, is_oauth_only, role, isbanned, created_at, updated_at)
VALUES 
('John Doe', 'john.doe@example.com', '$2a$10$Wat0XFWOiuhgmvcKlQP/Ju4BEc68a71QBcleV.u1.XWv7YJonLIEy', FALSE, 'user', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- 12345678

-- User 2: Admin user
INSERT INTO users (name, email, password, is_oauth_only, role, isbanned, created_at, updated_at)
VALUES 
('Jane Smith', 'jane.smith@example.com', '$2a$10$Wat0XFWOiuhgmvcKlQP/Ju4BEc68a71QBcleV.u1.XWv7YJonLIEy ', FALSE, 'admin', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User 3: OAuth-only user
INSERT INTO users (name, email, password, is_oauth_only, role, isbanned, created_at, updated_at)
VALUES 
('Alex Johnson', 'alex.johnson@example.com', NULL, TRUE, 'premium', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);