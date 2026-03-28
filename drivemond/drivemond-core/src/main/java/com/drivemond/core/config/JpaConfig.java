package com.drivemond.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Enables JPA auditing (populated by {@link com.drivemond.core.audit.AuditorAwareImpl})
 * and transaction management across all modules.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(basePackages = "com.drivemond")
public class JpaConfig {
    // Bean definitions are handled via Spring Boot auto-configuration;
    // this class exists only to enable the required annotations.
}
