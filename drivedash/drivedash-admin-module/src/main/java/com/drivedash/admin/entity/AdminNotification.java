package com.drivedash.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * In-app notification displayed to admins in the top-bar dropdown.
 * Maps to the {@code admin_notifications} table.
 *
 * <p>{@code model} holds the simple class name (e.g. {@code "TripRequest"})
 * and {@code modelId} is the UUID of the related record – a lightweight
 * polymorphic reference without a JPA join.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "admin_notifications",
    indexes = {
        @Index(name = "idx_an_is_seen", columnList = "is_seen"),
        @Index(name = "idx_an_model",   columnList = "model, model_id")
    }
)
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Simple class name of the related entity (e.g. {@code "TripRequest"}). */
    @Column(name = "model", nullable = false, length = 100)
    private String model;

    /** UUID of the related record. */
    @Column(name = "model_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID modelId;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Builder.Default
    @Column(name = "is_seen", nullable = false)
    private boolean seen = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}