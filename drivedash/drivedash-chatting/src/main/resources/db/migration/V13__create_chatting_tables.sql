-- =============================================================================
-- V13 – Chatting tables
-- Replaces Pusher/Laravel-WebSockets with Spring WebSocket + STOMP.
-- =============================================================================

CREATE TABLE IF NOT EXISTS channel_lists (
    id                  CHAR(36)        NOT NULL,
    channelable_id      CHAR(36)        NULL,
    channelable_type    VARCHAR(50)     NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP       NULL,
    created_by          CHAR(36)     NULL,
    updated_by          CHAR(36)     NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX uq_channel_channelable (channelable_id, channelable_type),
    INDEX idx_channel_lists_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS channel_users (
    id                  CHAR(36)        NOT NULL,
    channel_id          CHAR(36)        NOT NULL,
    user_id             CHAR(36)        NOT NULL,
    is_read             TINYINT(1)      NOT NULL DEFAULT 0,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP       NULL,
    created_by          CHAR(36)     NULL,
    updated_by          CHAR(36)     NULL,
    PRIMARY KEY (id),
    INDEX idx_channel_users_channel_id (channel_id),
    INDEX idx_channel_users_user_id (user_id),
    INDEX idx_channel_users_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS channel_conversations (
    id                  CHAR(36)        NOT NULL,
    channel_id          CHAR(36)        NOT NULL,
    user_id             CHAR(36)        NOT NULL,
    message             TEXT            NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP       NULL,
    created_by          CHAR(36)     NULL,
    updated_by          CHAR(36)     NULL,
    PRIMARY KEY (id),
    INDEX idx_channel_conversations_channel_id (channel_id),
    INDEX idx_channel_conversations_user_id (user_id),
    INDEX idx_channel_conversations_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS conversation_files (
    id                  CHAR(36)        NOT NULL,
    conversation_id     CHAR(36)        NOT NULL,
    file_name           VARCHAR(500)    NOT NULL,
    file_type           VARCHAR(50)     NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by          CHAR(36)     NULL,
    updated_by          CHAR(36)     NULL,
    PRIMARY KEY (id),
    INDEX idx_conversation_files_conversation_id (conversation_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
