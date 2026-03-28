-- =============================================================================
-- V6 – User Management tables
-- Adds extended user fields, role permissions, levels, driver details,
-- accounts, withdrawals, notifications, and time-tracking tables.
-- =============================================================================

-- ── Extend users table ────────────────────────────────────────────────────────
ALTER TABLE users
    ADD COLUMN user_level_id       CHAR(36)     NULL       AFTER updated_by,
    ADD COLUMN full_name           VARCHAR(191) NULL       AFTER user_level_id,
    ADD COLUMN identification_number VARCHAR(191) NULL     AFTER full_name,
    ADD COLUMN identification_type VARCHAR(25)  NULL       AFTER identification_number,
    ADD COLUMN identification_image JSON        NULL       AFTER identification_type,
    ADD COLUMN other_documents     JSON         NULL       AFTER identification_image,
    ADD COLUMN fcm_token           VARCHAR(191) NULL       AFTER other_documents,
    ADD COLUMN loyalty_points      DOUBLE       NOT NULL DEFAULT 0 AFTER fcm_token,
    ADD COLUMN failed_attempt      INT          NOT NULL DEFAULT 0 AFTER loyalty_points,
    ADD COLUMN is_temp_blocked     TINYINT(1)  NOT NULL DEFAULT 0 AFTER failed_attempt,
    ADD COLUMN blocked_at          TIMESTAMP   NULL       AFTER is_temp_blocked;

-- ── Extend roles table ────────────────────────────────────────────────────────
ALTER TABLE roles
    ADD COLUMN modules   JSON        NULL          AFTER name,
    ADD COLUMN is_active TINYINT(1)  NOT NULL DEFAULT 1 AFTER modules;

-- ── User Levels ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_levels (
    id                       CHAR(36)       NOT NULL,
    sequence                 INT            NOT NULL DEFAULT 0,
    name                     VARCHAR(191)   NOT NULL,
    reward_type              VARCHAR(20)    NOT NULL DEFAULT 'point',
    reward_amount            DECIMAL(15,2)  NULL,
    image                    VARCHAR(191)   NULL,
    targeted_ride            INT            NOT NULL DEFAULT 0,
    targeted_ride_point      INT            NOT NULL DEFAULT 0,
    targeted_amount          DOUBLE         NOT NULL DEFAULT 0,
    targeted_amount_point    INT            NOT NULL DEFAULT 0,
    targeted_cancel          INT            NOT NULL DEFAULT 0,
    targeted_cancel_point    INT            NOT NULL DEFAULT 0,
    targeted_review          INT            NOT NULL DEFAULT 0,
    targeted_review_point    INT            NOT NULL DEFAULT 0,
    user_type                VARCHAR(20)    NOT NULL COMMENT 'customer|driver',
    is_active                TINYINT(1)    NOT NULL DEFAULT 1,
    created_at               TIMESTAMP     NULL,
    updated_at               TIMESTAMP     NULL,
    deleted_at               TIMESTAMP     NULL,
    created_by               CHAR(36)   NULL,
    updated_by               CHAR(36)   NULL,
    PRIMARY KEY (id),
    INDEX idx_ul_user_type (user_type),
    INDEX idx_ul_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Level Accesses ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS level_accesses (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    level_id            CHAR(36)        NOT NULL,
    user_type           VARCHAR(50)     NOT NULL,
    bid                 TINYINT(1)     NOT NULL DEFAULT 0,
    see_destination     TINYINT(1)     NOT NULL DEFAULT 0,
    see_subtotal        TINYINT(1)     NOT NULL DEFAULT 0,
    see_level           TINYINT(1)     NOT NULL DEFAULT 0,
    create_hire_request TINYINT(1)     NOT NULL DEFAULT 0,
    created_at          TIMESTAMP      NULL,
    updated_at          TIMESTAMP      NULL,
    PRIMARY KEY (id),
    INDEX idx_la_level_id (level_id),
    CONSTRAINT fk_la_level FOREIGN KEY (level_id) REFERENCES user_levels (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── User Addresses ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_addresses (
    id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id               CHAR(36)        NULL,
    zone_id               CHAR(36)        NULL,
    latitude              VARCHAR(191)    NULL,
    longitude             VARCHAR(191)    NULL,
    city                  VARCHAR(191)    NULL,
    street                VARCHAR(191)    NULL,
    house                 VARCHAR(191)    NULL,
    zip_code              VARCHAR(50)     NULL,
    country               VARCHAR(100)    NULL,
    contact_person_name   VARCHAR(191)    NULL,
    contact_person_phone  VARCHAR(50)     NULL,
    address               TEXT            NULL,
    address_label         VARCHAR(100)    NULL,
    created_at            TIMESTAMP       NULL,
    updated_at            TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_ua_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── User Last Locations ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_last_locations (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    CHAR(36)        NULL,
    type       VARCHAR(30)     NULL COMMENT 'customer|driver',
    latitude   VARCHAR(191)    NULL,
    longitude  VARCHAR(191)    NULL,
    zone_id    CHAR(36)        NULL,
    created_at TIMESTAMP       NULL,
    updated_at TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_ull_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Driver Details ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS driver_details (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id             CHAR(36)        NOT NULL,
    is_online           TINYINT(1)     NOT NULL DEFAULT 0,
    availability_status VARCHAR(30)     NOT NULL DEFAULT 'unavailable',
    online              TIME            NULL,
    offline             TIME            NULL,
    online_time         DOUBLE(23,2)   NOT NULL DEFAULT 0,
    accepted            TIME            NULL,
    completed           TIME            NULL,
    start_driving       TIME            NULL,
    on_driving_time     DOUBLE(23,2)   NOT NULL DEFAULT 0,
    idle_time           DOUBLE(23,2)   NOT NULL DEFAULT 0,
    created_at          TIMESTAMP      NULL,
    updated_at          TIMESTAMP      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_dd_user_id (user_id),
    INDEX idx_dd_is_online (is_online)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Driver Time Logs ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS driver_time_logs (
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    driver_id         CHAR(36)        NOT NULL,
    date              DATE            NOT NULL,
    online            TIME            NULL,
    offline           TIME            NULL,
    online_time       DOUBLE(23,2)   NOT NULL DEFAULT 0,
    accepted          TIME            NULL,
    completed         TIME            NULL,
    start_driving     TIME            NULL,
    on_driving_time   DOUBLE(23,2)   NOT NULL DEFAULT 0,
    idle_time         DOUBLE(23,2)   NOT NULL DEFAULT 0,
    on_time_completed TINYINT(1)     NOT NULL DEFAULT 0,
    late_completed    TINYINT(1)     NOT NULL DEFAULT 0,
    late_pickup       TINYINT(1)     NOT NULL DEFAULT 0,
    created_at        TIMESTAMP      NULL,
    updated_at        TIMESTAMP      NULL,
    PRIMARY KEY (id),
    INDEX idx_dtl_driver_date (driver_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── User Level Histories ──────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_level_histories (
    id                        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_level_id             CHAR(36)        NOT NULL,
    user_id                   CHAR(36)        NOT NULL,
    user_type                 VARCHAR(20)     NOT NULL,
    completed_ride            INT             NOT NULL DEFAULT 0,
    ride_reward_status        TINYINT(1)     NOT NULL DEFAULT 0,
    total_amount              DECIMAL(15,2)  NOT NULL DEFAULT 0,
    amount_reward_status      TINYINT(1)     NOT NULL DEFAULT 0,
    cancellation_rate         DECIMAL(5,2)   NOT NULL DEFAULT 0,
    cancellation_reward_status TINYINT(1)    NOT NULL DEFAULT 0,
    reviews                   INT            NOT NULL DEFAULT 0,
    reviews_reward_status     TINYINT(1)     NOT NULL DEFAULT 0,
    is_level_reward_granted   TINYINT(1)     NOT NULL DEFAULT 0,
    deleted_at                TIMESTAMP      NULL,
    created_at                TIMESTAMP      NULL,
    updated_at                TIMESTAMP      NULL,
    PRIMARY KEY (id),
    INDEX idx_ulh_user_id (user_id),
    INDEX idx_ulh_level_id (user_level_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── User Accounts ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS user_accounts (
    id                  CHAR(36)        NOT NULL,
    user_id             CHAR(36)        NULL,
    payable_balance     DECIMAL(24,2)  NOT NULL DEFAULT 0,
    receivable_balance  DECIMAL(24,2)  NOT NULL DEFAULT 0,
    received_balance    DECIMAL(24,2)  NOT NULL DEFAULT 0,
    pending_balance     DECIMAL(24,2)  NOT NULL DEFAULT 0,
    wallet_balance      DECIMAL(24,2)  NOT NULL DEFAULT 0,
    total_withdrawn     DECIMAL(24,2)  NOT NULL DEFAULT 0,
    created_at          TIMESTAMP      NULL,
    updated_at          TIMESTAMP      NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_ua_user_id (user_id),
    INDEX idx_ua_user_id_lookup (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── App Notifications ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS app_notifications (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id         CHAR(36)        NOT NULL,
    ride_request_id CHAR(36)        NULL,
    title           VARCHAR(255)    NOT NULL,
    description     VARCHAR(500)    NOT NULL,
    type            VARCHAR(50)     NULL,
    action          VARCHAR(100)    NULL,
    is_read         TINYINT(1)     NOT NULL DEFAULT 0,
    created_at      TIMESTAMP       NULL,
    updated_at      TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_an_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Loyalty Points Histories ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS loyalty_points_histories (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    CHAR(36)        NULL,
    model      VARCHAR(100)    NOT NULL,
    model_id   CHAR(36)        NULL,
    points     DOUBLE          NOT NULL DEFAULT 0,
    type       VARCHAR(20)     NOT NULL COMMENT 'earned|spent',
    created_at TIMESTAMP       NULL,
    updated_at TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_lph_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Withdraw Methods ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS withdraw_methods (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    method_name   VARCHAR(191)    NOT NULL,
    method_fields JSON            NULL,
    is_default    TINYINT(1)     NOT NULL DEFAULT 0,
    is_active     TINYINT(1)     NOT NULL DEFAULT 1,
    created_at    TIMESTAMP       NULL,
    updated_at    TIMESTAMP       NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Withdraw Requests ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS withdraw_requests (
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id          CHAR(36)        NOT NULL,
    amount           DOUBLE          NOT NULL DEFAULT 0,
    method_id        BIGINT UNSIGNED NOT NULL,
    method_fields    JSON            NULL,
    note             TEXT            NULL,
    rejection_cause  TEXT            NULL,
    is_approved      TINYINT(1)     NULL COMMENT 'NULL=pending, 1=approved, 0=rejected',
    created_at       TIMESTAMP       NULL,
    updated_at       TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_wr_user_id (user_id),
    INDEX idx_wr_is_approved (is_approved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Time Tracks ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS time_tracks (
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id                 CHAR(36)        NOT NULL,
    date                    DATE            NOT NULL,
    total_online            INT             NOT NULL DEFAULT 0,
    total_offline           INT             NOT NULL DEFAULT 0,
    total_idle              INT             NOT NULL DEFAULT 0,
    total_driving           INT             NOT NULL DEFAULT 0,
    last_ride_started_at    TIME            NULL,
    last_ride_completed_at  TIME            NULL,
    created_at              TIMESTAMP       NULL,
    updated_at              TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_tt_user_date (user_id, date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Time Logs ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS time_logs (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    time_track_id BIGINT UNSIGNED NOT NULL,
    online_at     TIME            NOT NULL,
    offline_at    TIME            NULL,
    created_at    TIMESTAMP       NULL,
    updated_at    TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_tl_time_track_id (time_track_id),
    CONSTRAINT fk_tl_time_track FOREIGN KEY (time_track_id) REFERENCES time_tracks (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
