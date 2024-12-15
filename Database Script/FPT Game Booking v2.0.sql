-- Drop the database if it exists
DROP DATABASE IF EXISTS fpt_game_booking;

-- Create the database
CREATE DATABASE fpt_game_booking;

-- Create the 'users' table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255), -- Nullable for OAuth users
    is_oauth_only BOOLEAN DEFAULT FALSE NOT NULL,
    role VARCHAR(50) DEFAULT 'user' NOT NULL, -- admin, user, premium
    isbanned BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create the 'password_reset_tokens' table
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    isUsed BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create the 'invalidated_token' table
CREATE TABLE invalidated_token (
    id VARCHAR(255) PRIMARY KEY,
    expiry_time TIMESTAMP NOT NULL
);

-- Create the 'game_types' table 
CREATE TABLE game_types (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    game_name VARCHAR(255) NOT NULL, -- e.g., 'League of Legends', 'Valorant'
    rank VARCHAR(50),                -- e.g., 'Bronze', 'Gold', 'Platinum'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE game_rooms (
    id BIGSERIAL PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL, -- Name of the room
    game_name VARCHAR(255) NOT NULL, -- e.g., 'League of Legends', 'Valorant'
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL, -- User who created the room
    status VARCHAR(50) DEFAULT 'waiting' NOT NULL, -- Room status ('waiting', 'in_progress', 'completed')
    map_name VARCHAR(255) NOT NULL, -- Name of the map
    note VARCHAR(255), -- Room note
    rank_recommended VARCHAR(50), -- Rank recommended for players 
    game_mode VARCHAR(50) NOT NULL, -- Game mode (e.g., '5vs5', '1vs1')
    room_code VARCHAR(6) UNIQUE NOT NULL, -- Unique room code for access
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- When the room was created
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL -- When the room was last updated
);

CREATE TABLE game_room_players (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT REFERENCES game_rooms(id) ON DELETE CASCADE, -- References the game room
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE, -- References the user in the room
    team VARCHAR(10) NOT NULL, -- The team of the player ('team1' or 'team2')
    is_captain BOOLEAN DEFAULT FALSE NOT NULL, -- Whether the player is the captain
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE game_room_chats (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT REFERENCES game_rooms(id) ON DELETE CASCADE, -- The game room where the message was sent
    sender_id BIGINT REFERENCES users(id) ON DELETE CASCADE, -- The user who sent the message
    receiver_id BIGINT REFERENCES users(id), -- Null for "all" or "team" chat, or specific user ID for private messages
    team VARCHAR(10), -- 'team1' or 'team2' for team chat, NULL for "all" or private messages
    message TEXT NOT NULL, -- The chat message
    message_type VARCHAR(50) DEFAULT 'all' NOT NULL, -- 'all', 'team', or 'private'
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL -- Timestamp of the message
);

CREATE TABLE game_results (
    id BIGSERIAL PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL, -- Name of the game room
    game_name VARCHAR(255) NOT NULL, -- e.g., 'League of Legends', 'Valorant'
    winner_team VARCHAR(10) NOT NULL, -- 'team1' or 'team2'
    team1_score INT DEFAULT 0 NOT NULL, -- Score for team1
    team2_score INT DEFAULT 0 NOT NULL, -- Score for team2
    duration INTERVAL NOT NULL, -- Duration of the match
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL -- Match end time
);

CREATE TABLE game_result_users (
    id BIGSERIAL PRIMARY KEY,
    game_result_id BIGINT REFERENCES game_results(id) ON DELETE CASCADE, -- Links to the game result
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE, -- Links to the user
    team VARCHAR(10) NOT NULL, -- 'team1' or 'team2'
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL -- When the user joined the game
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE, -- Reference to the recipient (user)
    notification_type VARCHAR(50) NOT NULL, -- Type of notification (e.g., 'game_result', 'chat', 'invite')
    message TEXT NOT NULL, -- The content of the notification
    reference_link VARCHAR(255), -- Direct link to the referenced content (e.g., game result, game room)
    is_read BOOLEAN DEFAULT FALSE NOT NULL, -- Whether the notification has been read
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- When the notification was created
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL -- When the notification was last updated
);




