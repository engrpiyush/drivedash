-- =============================================================================
-- V10 – Transaction Management tables
-- user_accounts already created in V6 (user-management module).
-- This migration adds the transaction ledger table only.
-- =============================================================================

CREATE TABLE IF NOT EXISTS transactions (
    id              CHAR(36)        NOT NULL,
    attribute_id    CHAR(36)        NULL,
    attribute       VARCHAR(191)    NULL,
    debit           DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    credit          DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    balance         DECIMAL(24, 2)  NOT NULL DEFAULT 0.00,
    user_id         CHAR(36)        NULL,
    account         VARCHAR(191)    NULL,
    trx_ref_id      CHAR(36)        NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      CHAR(36)     NULL,
    updated_by      CHAR(36)     NULL,
    PRIMARY KEY (id),
    INDEX idx_transactions_user_id (user_id),
    INDEX idx_transactions_attribute (attribute),
    INDEX idx_transactions_account (account),
    INDEX idx_transactions_trx_ref_id (trx_ref_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
