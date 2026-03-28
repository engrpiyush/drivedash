-- ── Phase 9: Parcel Management ───────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS parcel_categories (
    id          CHAR(36)     NOT NULL PRIMARY KEY,
    name        VARCHAR(191) NOT NULL UNIQUE,
    description TEXT         NOT NULL,
    image       VARCHAR(191) NOT NULL,
    is_active   TINYINT      NOT NULL DEFAULT 1,
    deleted_at  TIMESTAMP    NULL,
    created_by  CHAR(36),
    updated_by  CHAR(36),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS parcel_weights (
    id         CHAR(36)      NOT NULL PRIMARY KEY,
    min_weight DECIMAL(10,2) NOT NULL DEFAULT 0,
    max_weight DECIMAL(10,2) NOT NULL DEFAULT 0,
    is_active  TINYINT       NOT NULL DEFAULT 1,
    deleted_at TIMESTAMP     NULL,
    created_by CHAR(36),
    updated_by CHAR(36),
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS parcel_information (
    id                 BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parcel_category_id CHAR(36) NOT NULL,
    trip_request_id    CHAR(36) NOT NULL,
    payer              VARCHAR(20),
    weight             DOUBLE,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS parcel_user_infomations (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trip_request_id CHAR(36)     NOT NULL,
    contact_number  VARCHAR(20)  NOT NULL,
    name            VARCHAR(191),
    address         VARCHAR(500),
    user_type       VARCHAR(20)  NOT NULL,  -- 'sender' | 'receiver'
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
