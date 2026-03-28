-- ── Phase 8: Trip Management ──────────────────────────────────────────────────
-- NOTE: MySQL POINT columns from the PHP migrations are replaced with separate
--       DOUBLE lat/lng columns for JPA compatibility.

CREATE TABLE IF NOT EXISTS trip_requests (
    id                       CHAR(36)       NOT NULL PRIMARY KEY,
    ref_id                   VARCHAR(20)    NOT NULL,
    customer_id              CHAR(36),
    driver_id                CHAR(36),
    vehicle_category_id      CHAR(36),
    vehicle_id               CHAR(36),
    zone_id                  CHAR(36),
    area_id                  CHAR(36),
    estimated_fare           DECIMAL(23,3)  NOT NULL DEFAULT 0,
    actual_fare              DECIMAL(23,3)  NOT NULL DEFAULT 0,
    estimated_distance       DOUBLE         NOT NULL DEFAULT 0,
    paid_fare                DECIMAL(23,3)  NOT NULL DEFAULT 0,
    actual_distance          DOUBLE,
    encoded_polyline         TEXT,
    accepted_by              VARCHAR(191),
    payment_method           VARCHAR(50),
    payment_status           VARCHAR(20)    DEFAULT 'unpaid',
    coupon_id                CHAR(36),
    coupon_amount            DECIMAL(23,3),
    note                     TEXT,
    entrance                 VARCHAR(191),
    otp                      VARCHAR(10),
    rise_request_count       INT            NOT NULL DEFAULT 0,
    type                     VARCHAR(30),
    current_status           VARCHAR(20)    NOT NULL DEFAULT 'pending',
    trip_cancellation_reason TEXT,
    checked                  TINYINT        NOT NULL DEFAULT 0,
    tips                     DOUBLE         NOT NULL DEFAULT 0,
    is_paused                TINYINT        NOT NULL DEFAULT 0,
    map_screenshot           VARCHAR(191),
    deleted_at               TIMESTAMP      NULL,
    created_by               CHAR(36),
    updated_by               CHAR(36),
    created_at               TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trip_status (
    id               BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trip_request_id  CHAR(36)     NOT NULL,
    customer_id      CHAR(36)     NOT NULL,
    driver_id        CHAR(36),
    pending          TIMESTAMP    NULL,
    accepted         TIMESTAMP    NULL,
    out_for_pickup   TIMESTAMP    NULL,
    picked_up        TIMESTAMP    NULL,
    ongoing          TIMESTAMP    NULL,
    completed        TIMESTAMP    NULL,
    cancelled        TIMESTAMP    NULL,
    failed           TIMESTAMP    NULL,
    note             TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Stores GPS breadcrumbs recorded during a trip
CREATE TABLE IF NOT EXISTS trip_routes (
    id               BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trip_request_id  CHAR(36) NOT NULL,
    latitude         DOUBLE   NOT NULL,
    longitude        DOUBLE   NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trip_request_fees (
    id               BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trip_request_id  CHAR(36)       NOT NULL,
    cancellation_fee DECIMAL(23,3)  NOT NULL DEFAULT 0,
    cancelled_by     VARCHAR(20),
    waiting_fee      DECIMAL(23,3)  NOT NULL DEFAULT 0,
    waited_by        VARCHAR(20),
    idle_fee         DECIMAL(23,3)  NOT NULL DEFAULT 0,
    delay_fee        DECIMAL(23,3)  NOT NULL DEFAULT 0,
    delayed_by       VARCHAR(20),
    vat_tax          DECIMAL(23,3)  NOT NULL DEFAULT 0,
    tips             DECIMAL(23,3)  NOT NULL DEFAULT 0,
    admin_commission DECIMAL(23,3)  NOT NULL DEFAULT 0,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trip_request_times (
    id                       BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trip_request_id          CHAR(36)  NOT NULL,
    estimated_time           DOUBLE    NOT NULL DEFAULT 0,
    actual_time              DOUBLE,
    waiting_time             DOUBLE,
    delay_time               DOUBLE,
    idle_timestamp           TIMESTAMP NULL,
    idle_time                DOUBLE,
    driver_arrival_time      DOUBLE,
    driver_arrival_timestamp TIMESTAMP NULL,
    driver_arrives_at        TIMESTAMP NULL,
    customer_arrives_at      TIMESTAMP NULL,
    created_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trip_request_coordinates (
    id                       BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trip_request_id          CHAR(36)  NOT NULL,
    pickup_lat               DOUBLE,
    pickup_lng               DOUBLE,
    pickup_address           VARCHAR(500),
    destination_lat          DOUBLE,
    destination_lng          DOUBLE,
    destination_address      VARCHAR(500),
    is_reached_destination   TINYINT   NOT NULL DEFAULT 0,
    intermediate_coordinates TEXT,
    int_lat_1                DOUBLE,
    int_lng_1                DOUBLE,
    is_reached_1             TINYINT   NOT NULL DEFAULT 0,
    int_lat_2                DOUBLE,
    int_lng_2                DOUBLE,
    is_reached_2             TINYINT   NOT NULL DEFAULT 0,
    intermediate_addresses   TEXT,
    start_lat                DOUBLE,
    start_lng                DOUBLE,
    drop_lat                 DOUBLE,
    drop_lng                 DOUBLE,
    driver_accept_lat        DOUBLE,
    driver_accept_lng        DOUBLE,
    customer_request_lat     DOUBLE,
    customer_request_lng     DOUBLE,
    created_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rejected_driver_requests (
    id               BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    trip_request_id  CHAR(36)  NOT NULL,
    user_id          CHAR(36)  NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS recent_addresses (
    id                   BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id              CHAR(36),
    zone_id              CHAR(36),
    pickup_lat           DOUBLE,
    pickup_lng           DOUBLE,
    pickup_address       VARCHAR(500),
    destination_lat      DOUBLE,
    destination_lng      DOUBLE,
    destination_address  VARCHAR(500),
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
