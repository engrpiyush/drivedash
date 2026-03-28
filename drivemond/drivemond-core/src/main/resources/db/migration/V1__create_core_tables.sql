-- =============================================================================
-- V1 – Core platform tables
-- Mirrors the base Laravel migrations (users, roles, tokens, jobs)
-- =============================================================================

-- ── Jobs (async queue) ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS jobs (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    queue       VARCHAR(255)    NOT NULL,
    payload     LONGTEXT        NOT NULL,
    attempts    TINYINT UNSIGNED NOT NULL DEFAULT 0,
    reserved_at INT UNSIGNED,
    available_at INT UNSIGNED   NOT NULL,
    created_at  INT UNSIGNED    NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_jobs_queue (queue)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS failed_jobs (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    uuid         CHAR(36)     NOT NULL,
    connection   TEXT            NOT NULL,
    queue        TEXT            NOT NULL,
    payload      LONGTEXT        NOT NULL,
    exception    LONGTEXT        NOT NULL,
    failed_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_failed_jobs_uuid (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Personal access tokens (Sanctum equivalent) ───────────────────────────────
CREATE TABLE IF NOT EXISTS personal_access_tokens (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    tokenable_type VARCHAR(255)    NOT NULL,
    tokenable_id   CHAR(36)     NOT NULL,
    name           VARCHAR(255)    NOT NULL,
    token          VARCHAR(64)     NOT NULL,
    abilities      TEXT,
    last_used_at   TIMESTAMP       NULL,
    expires_at     TIMESTAMP       NULL,
    created_at     TIMESTAMP       NULL,
    updated_at     TIMESTAMP       NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_personal_access_tokens_token (token),
    INDEX idx_pat_tokenable (tokenable_type, tokenable_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── OAuth tokens (Passport equivalent) ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS oauth_clients (
    id                   CHAR(36)  NOT NULL,
    user_id              CHAR(36)  NULL,
    name                 VARCHAR(255) NOT NULL,
    secret               VARCHAR(100) NULL,
    provider             VARCHAR(255) NULL,
    redirect             TEXT         NOT NULL,
    personal_access_client TINYINT(1) NOT NULL DEFAULT 0,
    password_client      TINYINT(1)  NOT NULL DEFAULT 0,
    revoked              TINYINT(1)  NOT NULL DEFAULT 0,
    created_at           TIMESTAMP   NULL,
    updated_at           TIMESTAMP   NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS oauth_access_tokens (
    id         VARCHAR(100) NOT NULL,
    user_id    CHAR(36)  NULL,
    client_id  CHAR(36)  NOT NULL,
    name       VARCHAR(255) NULL,
    scopes     TEXT,
    revoked    TINYINT(1)  NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NULL,
    updated_at TIMESTAMP   NULL,
    expires_at DATETIME    NULL,
    PRIMARY KEY (id),
    INDEX idx_oat_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS oauth_refresh_tokens (
    id              VARCHAR(100) NOT NULL,
    access_token_id VARCHAR(100) NOT NULL,
    revoked         TINYINT(1)  NOT NULL DEFAULT 0,
    expires_at      DATETIME    NULL,
    PRIMARY KEY (id),
    INDEX idx_ort_access_token_id (access_token_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Roles ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS roles (
    id         CHAR(36)     NOT NULL,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    NULL,
    updated_at TIMESTAMP    NULL,
    deleted_at TIMESTAMP    NULL,
    created_by CHAR(36)  NULL,
    updated_by CHAR(36)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Module / level access ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS module_accesses (
    id         CHAR(36)     NOT NULL,
    role_id    CHAR(36)     NOT NULL,
    module     VARCHAR(100) NOT NULL,
    can_read   TINYINT(1)  NOT NULL DEFAULT 0,
    can_write  TINYINT(1)  NOT NULL DEFAULT 0,
    can_delete TINYINT(1)  NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NULL,
    updated_at TIMESTAMP   NULL,
    PRIMARY KEY (id),
    INDEX idx_ma_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id               CHAR(36)     NOT NULL,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NULL,
    email            VARCHAR(191) NULL,
    phone            VARCHAR(30)  NULL,
    password         VARCHAR(255) NOT NULL,
    profile_image    VARCHAR(500) NULL,
    user_type        VARCHAR(30)  NOT NULL COMMENT 'super_admin|admin|employee|driver|customer',
    is_active        TINYINT(1)  NOT NULL DEFAULT 1,
    email_verified_at TIMESTAMP  NULL,
    phone_verified_at TIMESTAMP  NULL,
    referral_code    VARCHAR(20)  NULL,
    referred_by      CHAR(36)     NULL,
    remember_token   VARCHAR(100) NULL,
    created_at       TIMESTAMP   NULL,
    updated_at       TIMESTAMP   NULL,
    deleted_at       TIMESTAMP   NULL,
    created_by       CHAR(36) NULL,
    updated_by       CHAR(36) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email),
    UNIQUE KEY uq_users_phone (phone),
    INDEX idx_users_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── Role–User pivot ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS role_user (
    id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    role_id CHAR(36)        NOT NULL,
    user_id CHAR(36)        NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_role_user (role_id, user_id),
    INDEX idx_ru_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── OTP verifications ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS otp_verifications (
    id         CHAR(36)    NOT NULL,
    user_id    CHAR(36)    NOT NULL,
    otp        VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    verified   TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NULL,
    updated_at TIMESTAMP   NULL,
    PRIMARY KEY (id),
    INDEX idx_otp_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
