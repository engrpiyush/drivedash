-- ============================================================
-- Phase 5 – Vehicle Management
-- ============================================================

CREATE TABLE IF NOT EXISTS vehicle_brands
(
    id          CHAR(36)  NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NULL,
    image       VARCHAR(500) NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by  CHAR(36) NULL,
    updated_by  CHAR(36) NULL,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at  DATETIME(6)  NULL,
    CONSTRAINT uq_vehicle_brands_name UNIQUE (name),
    INDEX idx_vehicle_brands_active (is_active),
    INDEX idx_vehicle_brands_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vehicle_categories
(
    id          CHAR(36)  NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NULL,
    image       VARCHAR(500) NULL,
    type        VARCHAR(50)  NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by  CHAR(36) NULL,
    updated_by  CHAR(36) NULL,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at  DATETIME(6)  NULL,
    CONSTRAINT uq_vehicle_categories_name UNIQUE (name),
    INDEX idx_vehicle_categories_active (is_active),
    INDEX idx_vehicle_categories_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vehicle_models
(
    id                 CHAR(36)    NOT NULL PRIMARY KEY,
    name               VARCHAR(255)   NOT NULL,
    brand_id           CHAR(36)    NOT NULL,
    seat_capacity      INT            NULL,
    maximum_weight     DECIMAL(10, 2) NULL,
    hatch_bag_capacity INT            NULL,
    engine             VARCHAR(100)   NULL,
    description        TEXT           NULL,
    image              VARCHAR(500)   NULL,
    is_active          BOOLEAN        NOT NULL DEFAULT TRUE,
    created_by         CHAR(36)       NULL,
    updated_by         CHAR(36)       NULL,
    created_at         DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at         DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at         DATETIME(6)    NULL,
    CONSTRAINT uq_model_name_brand UNIQUE (name, brand_id),
    INDEX idx_vehicle_models_brand (brand_id),
    INDEX idx_vehicle_models_active (is_active),
    INDEX idx_vehicle_models_deleted (deleted_at),
    CONSTRAINT fk_vehicle_models_brand FOREIGN KEY (brand_id) REFERENCES vehicle_brands (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS vehicles
(
    id                    CHAR(36)  NOT NULL PRIMARY KEY,
    ref_id                VARCHAR(20)  NULL,
    brand_id              CHAR(36)  NOT NULL,
    model_id              CHAR(36)  NOT NULL,
    category_id           CHAR(36)  NOT NULL,
    licence_plate_number  VARCHAR(50)  NOT NULL,
    licence_expire_date   DATE         NULL,
    vin_number            VARCHAR(100) NULL,
    transmission          VARCHAR(100) NULL,
    fuel_type             VARCHAR(20)  NULL,
    ownership             VARCHAR(10)  NULL,
    driver_id             CHAR(36)  NULL,
    documents             JSON         NULL,
    is_active             BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by            CHAR(36) NULL,
    updated_by            CHAR(36) NULL,
    created_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at            DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at            DATETIME(6)  NULL,
    INDEX idx_vehicles_driver (driver_id),
    INDEX idx_vehicles_brand (brand_id),
    INDEX idx_vehicles_model (model_id),
    INDEX idx_vehicles_category (category_id),
    INDEX idx_vehicles_active (is_active),
    INDEX idx_vehicles_deleted (deleted_at),
    CONSTRAINT fk_vehicles_brand    FOREIGN KEY (brand_id)    REFERENCES vehicle_brands (id),
    CONSTRAINT fk_vehicles_model    FOREIGN KEY (model_id)    REFERENCES vehicle_models (id),
    CONSTRAINT fk_vehicles_category FOREIGN KEY (category_id) REFERENCES vehicle_categories (id),
    CONSTRAINT fk_vehicles_driver   FOREIGN KEY (driver_id)   REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
