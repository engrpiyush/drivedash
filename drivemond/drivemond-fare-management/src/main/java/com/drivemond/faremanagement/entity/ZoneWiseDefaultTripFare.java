package com.drivemond.faremanagement.entity;

import com.drivemond.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Stores the zone-level default fares used when category_wise_fare = false,
 * or as defaults for the per-category TripFare rows when category_wise_fare = true.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zone_wise_default_trip_fares")
public class ZoneWiseDefaultTripFare extends BaseAuditEntity {

    @Column(name = "zone_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID zoneId;

    @Builder.Default @Column(name = "base_fare", nullable = false) private double baseFare = 0;
    @Builder.Default @Column(name = "base_fare_per_km", nullable = false) private double baseFarePerKm = 0;
    @Builder.Default @Column(name = "waiting_fee_per_min", nullable = false) private double waitingFeePerMin = 0;
    @Builder.Default @Column(name = "cancellation_fee_percent", nullable = false) private double cancellationFeePercent = 0;
    @Builder.Default @Column(name = "min_cancellation_fee", nullable = false) private double minCancellationFee = 0;
    @Builder.Default @Column(name = "idle_fee_per_min", nullable = false) private double idleFeePerMin = 0;
    @Builder.Default @Column(name = "trip_delay_fee_per_min", nullable = false) private double tripDelayFeePerMin = 0;
    @Builder.Default @Column(name = "penalty_fee_for_cancel", nullable = false) private double penaltyFeeForCancel = 0;
    @Builder.Default @Column(name = "fee_add_to_next", nullable = false) private double feeAddToNext = 0;

    /** true = also configure per-category TripFare rows for this zone */
    @Builder.Default
    @Column(name = "category_wise_fare", nullable = false)
    private boolean categoryWiseFare = false;
}
