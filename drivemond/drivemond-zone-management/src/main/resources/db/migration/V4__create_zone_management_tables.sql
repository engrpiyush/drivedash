-- ============================================================
-- Phase 4 – Zone Management
-- ============================================================

CREATE TABLE IF NOT EXISTS zones
(
    id         CHAR(36)  NOT NULL PRIMARY KEY,
    name       VARCHAR(191) NOT NULL,
    coordinates GEOMETRY    NULL COMMENT 'Polygon drawn on Google Maps',
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by CHAR(36) NULL,
    updated_by CHAR(36) NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6)  NULL,

    CONSTRAINT uq_zones_name UNIQUE (name),
    INDEX idx_zones_is_active (is_active),
    INDEX idx_zones_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
