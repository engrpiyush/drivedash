package com.drivemond.auth.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds {@code app.jwt.*} properties from {@code application.yml}.
 * Using a dedicated properties class keeps JWT config type-safe
 * and testable without touching the environment.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** 256-bit (minimum) HMAC-SHA secret key. */
    private String secret;

    /** Access token lifetime in milliseconds (default 24 h). */
    private long expirationMs = 86_400_000L;

    /** Refresh token lifetime in milliseconds (default 7 days). */
    private long refreshExpirationMs = 604_800_000L;
}
