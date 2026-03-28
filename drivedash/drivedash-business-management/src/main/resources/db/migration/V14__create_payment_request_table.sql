-- =============================================================================
-- V14 – Payment gateway infrastructure
--
-- Gateway configurations are stored in business_settings (created in V2)
-- using settings_type = 'PAYMENT_CONFIG' and key_name = <gateway-slug>.
--
-- This migration adds only the payment_requests tracking table.
-- =============================================================================

CREATE TABLE IF NOT EXISTS payment_requests (
    id                 CHAR(36)       NOT NULL,
    user_id            CHAR(36)       NOT NULL                   COMMENT 'customer or driver UUID',
    payment_platform   VARCHAR(100)   NOT NULL                   COMMENT 'gateway slug, e.g. stripe, paypal',
    attribute          VARCHAR(191)   NULL                       COMMENT 'wallet | trip_request | parcel',
    attribute_id       CHAR(36)       NULL                       COMMENT 'UUID of the associated entity',
    trx_ref_id         CHAR(36)       NULL                       COMMENT 'internal transaction reference',
    payer_id           VARCHAR(191)   NULL                       COMMENT 'gateway-side payer identifier',
    payment_method     VARCHAR(100)   NULL,
    amount             DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    currency_code      VARCHAR(10)    NOT NULL DEFAULT 'USD',
    external_intent_id VARCHAR(255)   NULL                       COMMENT 'gateway-side intent / order id',
    is_paid            TINYINT(1)     NOT NULL DEFAULT 0,
    created_at         TIMESTAMP      NULL,
    updated_at         TIMESTAMP      NULL,
    created_by         CHAR(36)    NULL,
    updated_by         CHAR(36)    NULL,
    PRIMARY KEY (id),
    INDEX idx_pr_user_id  (user_id),
    INDEX idx_pr_trx_ref  (trx_ref_id),
    INDEX idx_pr_platform (payment_platform),
    INDEX idx_pr_paid     (is_paid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
