-- =============================================================================
-- V11 – Promotion Management tables
-- =============================================================================

CREATE TABLE IF NOT EXISTS banner_setups (
    id                  CHAR(36)        NOT NULL,
    name                VARCHAR(191)    NOT NULL,
    description         TEXT            NULL,
    time_period         VARCHAR(191)    NULL,
    display_position    VARCHAR(191)    NULL,
    redirect_link       VARCHAR(500)    NULL,
    banner_group        VARCHAR(191)    NULL,
    start_date          DATE            NULL,
    end_date            DATE            NULL,
    image               VARCHAR(500)    NULL,
    total_redirection   DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    is_active           TINYINT(1)      NOT NULL DEFAULT 1,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP       NULL,
    created_by          CHAR(36)     NULL,
    updated_by          CHAR(36)     NULL,
    PRIMARY KEY (id),
    INDEX idx_banner_setups_is_active (is_active),
    INDEX idx_banner_setups_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS coupon_setups (
    id                  CHAR(36)        NOT NULL,
    name                VARCHAR(50)     NULL,
    description         VARCHAR(255)    NULL,
    user_id             CHAR(36)        NULL,
    user_level_id       CHAR(36)        NULL,
    min_trip_amount     DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    max_coupon_amount   DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    coupon              DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    amount_type         VARCHAR(15)     NOT NULL DEFAULT 'percentage',
    coupon_type         VARCHAR(15)     NOT NULL DEFAULT 'default',
    coupon_code         VARCHAR(30)     NULL,
    `limit`             INT             NULL,
    start_date          DATE            NULL,
    end_date            DATE            NULL,
    rules               VARCHAR(191)    NULL DEFAULT 'default',
    total_used          DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    total_amount        DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    is_active           TINYINT(1)      NOT NULL DEFAULT 1,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMP       NULL,
    created_by          CHAR(36)     NULL,
    updated_by          CHAR(36)     NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX uq_coupon_code (coupon_code),
    INDEX idx_coupon_setups_is_active (is_active),
    INDEX idx_coupon_setups_deleted_at (deleted_at),
    INDEX idx_coupon_setups_user_id (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS coupon_setup_vehicle_category (
    coupon_setup_id     CHAR(36)        NOT NULL,
    vehicle_category_id CHAR(36)        NOT NULL,
    PRIMARY KEY (coupon_setup_id, vehicle_category_id),
    INDEX idx_csv_coupon_id (coupon_setup_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
