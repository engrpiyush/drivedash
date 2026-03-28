package com.drivedash.faremanagement.entity;

import com.drivedash.core.entity.BaseAuditEntity;
import com.drivedash.vehicle.entity.VehicleCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Per-zone, per-vehicle-category trip fare configuration.
 * Only used when {@link ZoneWiseDefaultTripFare#isCategoryWiseFare()} is true.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trip_fares",
       uniqueConstraints = @UniqueConstraint(name = "uq_tf_zone_category",
               columnNames = {"zone_id", "vehicle_category_id"}))
public class TripFare extends BaseAuditEntity {

    @Column(name = "zone_wise_default_trip_fare_id", columnDefinition = "CHAR(36)")
    private UUID zoneWiseDefaultTripFareId;

    @Column(name = "zone_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID zoneId;

    @Column(name = "vehicle_category_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID vehicleCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_category_id", insertable = false, updatable = false)
    private VehicleCategory vehicleCategory;

    @Builder.Default @Column(name = "base_fare", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseFare = BigDecimal.ZERO;

    @Builder.Default @Column(name = "base_fare_per_km", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseFarePerKm = BigDecimal.ZERO;

    @Builder.Default @Column(name = "waiting_fee_per_min", nullable = false, precision = 15, scale = 2)
    private BigDecimal waitingFeePerMin = BigDecimal.ZERO;

    @Builder.Default @Column(name = "cancellation_fee_percent", nullable = false, precision = 15, scale = 2)
    private BigDecimal cancellationFeePercent = BigDecimal.ZERO;

    @Builder.Default @Column(name = "min_cancellation_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal minCancellationFee = BigDecimal.ZERO;

    @Builder.Default @Column(name = "idle_fee_per_min", nullable = false, precision = 15, scale = 2)
    private BigDecimal idleFeePerMin = BigDecimal.ZERO;

    @Builder.Default @Column(name = "trip_delay_fee_per_min", nullable = false, precision = 15, scale = 2)
    private BigDecimal tripDelayFeePerMin = BigDecimal.ZERO;

    @Builder.Default @Column(name = "penalty_fee_for_cancel", nullable = false, precision = 15, scale = 2)
    private BigDecimal penaltyFeeForCancel = BigDecimal.ZERO;

    @Builder.Default @Column(name = "fee_add_to_next", nullable = false, precision = 15, scale = 2)
    private BigDecimal feeAddToNext = BigDecimal.ZERO;
}
