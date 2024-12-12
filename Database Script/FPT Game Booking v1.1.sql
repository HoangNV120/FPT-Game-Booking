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
    role VARCHAR(50) DEFAULT 'user' NOT NULL, -- admin, USER, premium
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

