-- =============================================================================
-- V7 – Fare Management tables
-- Zone-wise default trip fares, per-category trip fares, parcel fares
-- =============================================================================

-- ── Zone-Wise Default Trip Fares ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS zone_wise_default_trip_fares (
    id                       CHAR(36)       NOT NULL,
    zone_id                  CHAR(36)       NOT NULL,
    base_fare                DOUBLE         NOT NULL DEFAULT 0,
    base_fare_per_km         DOUBLE         NOT NULL DEFAULT 0,
    waiting_fee_per_min      DOUBLE         NOT NULL DEFAULT 0,
    cancellation_fee_percent DOUBLE         NOT NULL DEFAULT 0,
    min_cancellation_fee     DOUBLE         NOT NULL DEFAULT 0,
    idle_fee_per_min         DOUBLE         NOT NULL DEFAULT 0,
    trip_delay_fee_per_min   DOUBLE         NOT NULL DEFAULT 0,
    penalty_fee_for_cancel   DOUBLE         NOT NULL DEFAULT 0,
    fee_add_to_next          DOUBLE         NOT NULL DEFAULT 0,
    category_wise_fare       TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '1 = use per-category TripFare rows',
    created_at               TIMESTAMP     NULL,
    updated_at               TIMESTAMP     NULL,
    created_by               CHAR(36)   NULL,
    updated_by               CHAR(36)   NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_zwdtf_zone_id (zone_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Trip Fares (per category) ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS trip_fares (
    id                            CHAR(36)       NOT NULL,
    zone_wise_default_trip_fare_id CHAR(36)       NULL,
    zone_id                       CHAR(36)       NOT NULL,
    vehicle_category_id           CHAR(36)       NOT NULL,
    base_fare                     DECIMAL(15,2)  NOT NULL DEFAULT 0,
    base_fare_per_km              DECIMAL(15,2)  NOT NULL DEFAULT 0,
    waiting_fee_per_min           DECIMAL(15,2)  NOT NULL DEFAULT 0,
    cancellation_fee_percent      DECIMAL(15,2)  NOT NULL DEFAULT 0,
    min_cancellation_fee          DECIMAL(15,2)  NOT NULL DEFAULT 0,
    idle_fee_per_min              DECIMAL(15,2)  NOT NULL DEFAULT 0,
    trip_delay_fee_per_min        DECIMAL(15,2)  NOT NULL DEFAULT 0,
    penalty_fee_for_cancel        DECIMAL(15,2)  NOT NULL DEFAULT 0,
    fee_add_to_next               DECIMAL(15,2)  NOT NULL DEFAULT 0,
    created_at                    TIMESTAMP      NULL,
    updated_at                    TIMESTAMP      NULL,
    created_by                    CHAR(36)    NULL,
    updated_by                    CHAR(36)    NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_tf_zone_category (zone_id, vehicle_category_id),
    INDEX idx_tf_zone_id (zone_id),
    INDEX idx_tf_default_fare_id (zone_wise_default_trip_fare_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Parcel Fares ──────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS parcel_fares (
    id                       CHAR(36)      NOT NULL,
    zone_id                  CHAR(36)      NULL,
    base_fare                DECIMAL(15,2) NOT NULL DEFAULT 0,
    base_fare_per_km         DECIMAL(15,2) NOT NULL DEFAULT 0,
    cancellation_fee_percent DECIMAL(15,2) NOT NULL DEFAULT 0,
    min_cancellation_fee     DECIMAL(15,2) NOT NULL DEFAULT 0,
    created_at               TIMESTAMP     NULL,
    updated_at               TIMESTAMP     NULL,
    deleted_at               TIMESTAMP     NULL,
    created_by               CHAR(36)   NULL,
    updated_by               CHAR(36)   NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_pf_zone_id (zone_id),
    INDEX idx_pf_zone_id (zone_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Parcel Fare Weights (per weight+category within a parcel fare) ─────────────
CREATE TABLE IF NOT EXISTS parcel_fare_weights (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    parcel_fare_id      CHAR(36)        NOT NULL,
    parcel_weight_id    CHAR(36)        NULL,
    parcel_category_id  CHAR(36)        NULL,
    zone_id             CHAR(36)        NULL,
    base_fare           DOUBLE          NOT NULL DEFAULT 0,
    fare_per_km         DECIMAL(15,2)   NOT NULL DEFAULT 0,
    created_at          TIMESTAMP       NULL,
    updated_at          TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_pfw_parcel_fare_id (parcel_fare_id),
    CONSTRAINT fk_pfw_parcel_fare FOREIGN KEY (parcel_fare_id) REFERENCES parcel_fares (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Fare Biddings ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS fare_biddings (
    id               CHAR(36)       NOT NULL,
    trip_request_id  CHAR(36)       NULL,
    driver_id        CHAR(36)       NOT NULL,
    customer_id      CHAR(36)       NOT NULL,
    bid_fare         DECIMAL(15,2)  NOT NULL DEFAULT 0,
    is_ignored       TINYINT(1)    NOT NULL DEFAULT 0,
    created_at       TIMESTAMP      NULL,
    updated_at       TIMESTAMP      NULL,
    created_by               CHAR(36)   NULL,
    updated_by               CHAR(36)   NULL,
    PRIMARY KEY (id),
    INDEX idx_fb_trip_request_id (trip_request_id),
    INDEX idx_fb_driver_id (driver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Fare Bidding Logs ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS fare_bidding_logs (
    id               CHAR(36)       NOT NULL,
    trip_request_id  CHAR(36)       NULL,
    driver_id        CHAR(36)       NULL,
    customer_id      CHAR(36)       NULL,
    bid_fare         DECIMAL(15,2)  NULL,
    is_ignored       TINYINT(1)    NOT NULL DEFAULT 0,
    created_at       TIMESTAMP      NULL,
    updated_at       TIMESTAMP      NULL,
    created_by               CHAR(36)   NULL,
    updated_by               CHAR(36)   NULL,
    PRIMARY KEY (id),
    INDEX idx_fbl_trip_request_id (trip_request_id),
    INDEX idx_fbl_driver_id (driver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
