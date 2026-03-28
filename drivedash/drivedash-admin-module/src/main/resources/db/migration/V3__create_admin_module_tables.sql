-- =============================================================================
-- V3 – Admin Module tables
-- Mirrors Modules/AdminModule/Database/Migrations/*
-- =============================================================================

-- ── Activity logs ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS activity_logs (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    logable_id   CHAR(36)        NOT NULL COMMENT 'UUID of the modified entity',
    logable_type VARCHAR(191)    NOT NULL COMMENT 'Simple class name of the entity',
    edited_by    CHAR(36)        NOT NULL COMMENT 'UUID of the acting user',
    before_state JSON            NULL     COMMENT 'Entity state before mutation',
    after_state  JSON            NULL     COMMENT 'Entity state after mutation',
    user_type    VARCHAR(30)     NULL,
    created_at   TIMESTAMP       NULL,
    updated_at   TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_al_logable    (logable_type, logable_id),
    INDEX idx_al_edited_by  (edited_by),
    INDEX idx_al_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Admin notifications ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS admin_notifications (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    model      VARCHAR(100)    NOT NULL COMMENT 'e.g. TripRequest, User',
    model_id   CHAR(36)        NOT NULL COMMENT 'UUID of the related record',
    message    VARCHAR(500)    NOT NULL,
    is_seen    TINYINT(1)      NOT NULL DEFAULT 0,
    created_at TIMESTAMP       NULL,
    updated_at TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_an_is_seen    (is_seen),
    INDEX idx_an_model      (model, model_id),
    INDEX idx_an_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
