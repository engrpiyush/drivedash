package com.drivemond.business.entity;

import com.drivemond.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A social media link shown on the landing page.
 * Extends {@link BaseEntity} because the Laravel migration includes {@code softDeletes()}.
 * Maps to the {@code social_links} table.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "social_links")
public class SocialLink extends BaseEntity {

    @Column(name = "name", nullable = false, length = 191)
    private String name;

    @Column(name = "link", nullable = false, length = 191)
    private String link;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
