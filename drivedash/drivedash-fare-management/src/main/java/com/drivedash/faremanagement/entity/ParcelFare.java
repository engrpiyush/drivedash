package com.drivedash.faremanagement.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parcel_fares")
public class ParcelFare extends BaseEntity {

    @Column(name = "zone_id", columnDefinition = "CHAR(36)")
    private UUID zoneId;

    @Builder.Default @Column(name = "base_fare", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseFare = BigDecimal.ZERO;

    @Builder.Default @Column(name = "base_fare_per_km", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseFarePerKm = BigDecimal.ZERO;

    @Builder.Default @Column(name = "cancellation_fee_percent", nullable = false, precision = 15, scale = 2)
    private BigDecimal cancellationFeePercent = BigDecimal.ZERO;

    @Builder.Default @Column(name = "min_cancellation_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal minCancellationFee = BigDecimal.ZERO;
}
