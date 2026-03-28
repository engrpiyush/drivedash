package com.drivemond.faremanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Per-weight / per-category fare within a parcel fare zone configuration.
 * parcel_weight_id and parcel_category_id are FK stubs resolved in Phase 9 (ParcelManagement).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parcel_fare_weights")
@EntityListeners(AuditingEntityListener.class)
public class ParcelFareWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "parcel_fare_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID parcelFareId;

    @Column(name = "parcel_weight_id", columnDefinition = "CHAR(36)")
    private UUID parcelWeightId;

    @Column(name = "parcel_category_id", columnDefinition = "CHAR(36)")
    private UUID parcelCategoryId;

    @Column(name = "zone_id", columnDefinition = "CHAR(36)")
    private UUID zoneId;

    @Builder.Default
    @Column(name = "base_fare", nullable = false)
    private double baseFare = 0;

    @Builder.Default @Column(name = "fare_per_km", nullable = false, precision = 15, scale = 2)
    private BigDecimal farePerKm = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
