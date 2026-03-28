package com.drivedash.faremanagement.entity;

import com.drivedash.core.entity.BaseAuditEntity;
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
@Table(name = "fare_biddings")
public class FareBidding extends BaseAuditEntity {

    @Column(name = "trip_request_id", columnDefinition = "CHAR(36)")
    private UUID tripRequestId;

    @Column(name = "driver_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID driverId;

    @Column(name = "customer_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID customerId;

    @Builder.Default @Column(name = "bid_fare", nullable = false, precision = 15, scale = 2)
    private BigDecimal bidFare = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "is_ignored", nullable = false)
    private boolean ignored = false;
}
