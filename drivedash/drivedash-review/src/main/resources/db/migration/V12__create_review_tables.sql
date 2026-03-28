-- =============================================================================
-- V12 – Review tables
-- =============================================================================

CREATE TABLE IF NOT EXISTS reviews (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    trip_request_id     CHAR(36)        NULL,
    given_by            CHAR(36)        NULL,
    received_by         CHAR(36)        NULL,
    trip_type           VARCHAR(30)     NULL,
    rating              TINYINT         NOT NULL DEFAULT 1,
    feedback            TEXT            NULL,
    images              JSON            NULL,
    is_saved            TINYINT(1)      NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP       NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_reviews_given_by (given_by),
    INDEX idx_reviews_received_by (received_by),
    INDEX idx_reviews_trip_request_id (trip_request_id),
    INDEX idx_reviews_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
