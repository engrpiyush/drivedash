package com.drivemond.trip.entity;

import com.drivemond.auth.entity.User;
import com.drivemond.core.entity.BaseEntity;
import com.drivemond.vehicle.entity.VehicleCategory;
import com.drivemond.zone.entity.Zone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trip_requests")
public class TripRequest extends BaseEntity {

    @Column(name = "ref_id", nullable = false, length = 20)
    private String refId;

    @Column(name = "customer_id", columnDefinition = "CHAR(36)")
    private UUID customerId;

    @Column(name = "driver_id", columnDefinition = "CHAR(36)")
    private UUID driverId;

    @Column(name = "vehicle_category_id", columnDefinition = "CHAR(36)")
    private UUID vehicleCategoryId;

    @Column(name = "vehicle_id", columnDefinition = "CHAR(36)")
    private UUID vehicleId;

    @Column(name = "zone_id", columnDefinition = "CHAR(36)")
    private UUID zoneId;

    @Column(name = "area_id", columnDefinition = "CHAR(36)")
    private UUID areaId;

    @Builder.Default
    @Column(name = "estimated_fare", nullable = false, precision = 23, scale = 3)
    private BigDecimal estimatedFare = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "actual_fare", nullable = false, precision = 23, scale = 3)
    private BigDecimal actualFare = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "estimated_distance", nullable = false)
    private double estimatedDistance = 0;

    @Builder.Default
    @Column(name = "paid_fare", nullable = false, precision = 23, scale = 3)
    private BigDecimal paidFare = BigDecimal.ZERO;

    @Column(name = "actual_distance")
    private Double actualDistance;

    @Column(name = "encoded_polyline", columnDefinition = "TEXT")
    private String encodedPolyline;

    @Column(name = "accepted_by", length = 191)
    private String acceptedBy;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Builder.Default
    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "unpaid";

    @Column(name = "coupon_id", columnDefinition = "CHAR(36)")
    private UUID couponId;

    @Column(name = "coupon_amount", precision = 23, scale = 3)
    private BigDecimal couponAmount;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "entrance", length = 191)
    private String entrance;

    @Column(name = "otp", length = 10)
    private String otp;

    @Builder.Default
    @Column(name = "rise_request_count", nullable = false)
    private int riseRequestCount = 0;

    /** "ride" | "parcel" | "hire" */
    @Column(name = "type", length = 30)
    private String type;

    @Builder.Default
    @Column(name = "current_status", nullable = false, length = 20)
    private String currentStatus = "pending";

    @Column(name = "trip_cancellation_reason", columnDefinition = "TEXT")
    private String tripCancellationReason;

    @Builder.Default
    @Column(name = "checked", nullable = false)
    private boolean checked = false;

    @Builder.Default
    @Column(name = "tips", nullable = false)
    private double tips = 0;

    @Builder.Default
    @Column(name = "is_paused", nullable = false)
    private boolean isPaused = false;

    @Column(name = "map_screenshot", length = 191)
    private String mapScreenshot;

    // ── Read-only associations for display ───────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", insertable = false, updatable = false)
    private User driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_category_id", insertable = false, updatable = false)
    private VehicleCategory vehicleCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", insertable = false, updatable = false)
    private Zone zone;
}
