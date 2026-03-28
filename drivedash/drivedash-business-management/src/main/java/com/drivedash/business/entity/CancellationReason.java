package com.drivedash.business.entity;

import com.drivedash.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A pre-defined reason a driver or customer may give when cancelling a trip.
 * Maps to the {@code cancellation_reasons} table.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "cancellation_reasons",
    indexes = {
        @Index(name = "idx_cr_type_user", columnList = "cancellation_type, user_type")
    }
)
public class CancellationReason extends BaseAuditEntity {

    @Column(name = "title", nullable = false, columnDefinition = "LONGTEXT")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_type", nullable = false, columnDefinition = "VARCHAR(30)")
    private CancellationType cancellationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, columnDefinition = "VARCHAR(30)")
    private CancellationUserType userType;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
