CREATE TABLE IF NOT EXISTS chat_sessions (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    user_id CHAR(36) NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id CHAR(36) PRIMARY KEY,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    user_id CHAR(36) NOT NULL,
    session_id CHAR(36) NULL,
    role VARCHAR(20) NOT NULL,
    content VARCHAR(1000) NOT NULL
);

CREATE INDEX idx_chat_messages_user_created ON chat_messages (user_id, created_at);


