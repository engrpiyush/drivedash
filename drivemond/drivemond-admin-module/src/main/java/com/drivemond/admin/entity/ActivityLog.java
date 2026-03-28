package com.drivemond.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Audit trail for all admin-visible data mutations.
 *
 * <p>Uses a Long auto-increment PK (matching Laravel's {@code $table->id()}).
 * The {@code logableId} / {@code logableType} pair forms a polymorphic reference –
 * the same pattern as Laravel's {@code morphTo()} but implemented without
 * a JPA association to avoid coupling all modules here.
 *
 * <p>{@code beforeState} and {@code afterState} store a JSON snapshot of the entity
 * state taken by {@link com.drivemond.admin.aspect.ActivityLoggingAspect}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "activity_logs",
    indexes = {
        @Index(name = "idx_al_logable", columnList = "logable_type, logable_id"),
        @Index(name = "idx_al_edited_by", columnList = "edited_by")
    }
)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** UUID of the entity that was modified (polymorphic FK). */
    @Column(name = "logable_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID logableId;

    /** Fully-qualified Java class name of the modified entity. */
    @Column(name = "logable_type", nullable = false, length = 191)
    private String logableType;

    /** UUID of the admin/employee who made the change. */
    @Column(name = "edited_by", nullable = false, columnDefinition = "CHAR(36)")
    private UUID editedBy;

    /** JSON snapshot of the entity state before the mutation. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_state", columnDefinition = "JSON")
    private Map<String, Object> beforeState;

    /** JSON snapshot of the entity state after the mutation. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_state", columnDefinition = "JSON")
    private Map<String, Object> afterState;

    /** User type of the actor (e.g. {@code "super_admin"}, {@code "admin"}, {@code "employee"}). */
    @Column(name = "user_type", length = 30)
    private String userType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}