-- =============================================================================
-- V2 – Business Management tables
-- Mirrors Modules/BusinessManagement/Database/Migrations/*
-- =============================================================================

-- ── Business settings (flexible key-value JSON store) ────────────────────────
CREATE TABLE IF NOT EXISTS business_settings (
    id           CHAR(36)     NOT NULL,
    key_name     VARCHAR(191) NOT NULL,
    value        JSON         NOT NULL,
    settings_type VARCHAR(191) NOT NULL,
    created_at   TIMESTAMP    NULL,
    updated_at   TIMESTAMP    NULL,
    created_by   CHAR(36)  NULL,
    updated_by   CHAR(36)  NULL,
    PRIMARY KEY (id),
    INDEX idx_bs_key_type (key_name, settings_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Notification settings ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notification_settings (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name       VARCHAR(191)    NOT NULL,
    push       TINYINT(1)      NOT NULL DEFAULT 0,
    email      TINYINT(1)      NOT NULL DEFAULT 0,
    created_at TIMESTAMP       NULL,
    updated_at TIMESTAMP       NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_ns_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Firebase push notification config ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS firebase_push_notifications (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name       VARCHAR(191)    NOT NULL,
    value      VARCHAR(191)    NULL,
    status     TINYINT(1)      NOT NULL DEFAULT 0,
    created_at TIMESTAMP       NULL,
    updated_at TIMESTAMP       NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_fpn_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Social links ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS social_links (
    id         CHAR(36)     NOT NULL,
    name       VARCHAR(191) NOT NULL,
    link       VARCHAR(191) NOT NULL,
    is_active  TINYINT(1)  NOT NULL DEFAULT 1,
    deleted_at TIMESTAMP   NULL,
    created_at TIMESTAMP   NULL,
    updated_at TIMESTAMP   NULL,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Cancellation reasons ──────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cancellation_reasons (
    id                CHAR(36)     NOT NULL,
    title             LONGTEXT     NOT NULL,
    cancellation_type VARCHAR(30)  NOT NULL COMMENT 'ONGOING_RIDE|ACCEPTED_RIDE',
    user_type         VARCHAR(30)  NOT NULL COMMENT 'DRIVER|CUSTOMER',
    is_active         TINYINT(1)  NOT NULL DEFAULT 1,
    created_at        TIMESTAMP   NULL,
    updated_at        TIMESTAMP   NULL,
    created_by        CHAR(36) NULL,
    updated_by        CHAR(36) NULL,
    PRIMARY KEY (id),
    INDEX idx_cr_type_user (cancellation_type, user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
