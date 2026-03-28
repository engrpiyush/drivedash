package com.drivemond.auth.entity;

import com.drivemond.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Role entity – maps to the {@code roles} table.
 *
 * <p>Role names follow the Spring Security convention of {@code ROLE_} prefix
 * (e.g., {@code ROLE_ADMIN}, {@code ROLE_DRIVER}), which allows using
 * {@code hasRole("ADMIN")} directly in security expressions.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modules", columnDefinition = "JSON")
    private List<String> modules;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
