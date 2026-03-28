package com.drivedash.trip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trip_request_fees")
@EntityListeners(AuditingEntityListener.class)
public class TripRequestFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_request_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID tripRequestId;

    @Builder.Default @Column(name = "cancellation_fee", nullable = false, precision = 23, scale = 3)
    private BigDecimal cancellationFee = BigDecimal.ZERO;

    @Column(name = "cancelled_by", length = 20)
    private String cancelledBy;

    @Builder.Default @Column(name = "waiting_fee", nullable = false, precision = 23, scale = 3)
    private BigDecimal waitingFee = BigDecimal.ZERO;

    @Column(name = "waited_by", length = 20)
    private String waitedBy;

    @Builder.Default @Column(name = "idle_fee", nullable = false, precision = 23, scale = 3)
    private BigDecimal idleFee = BigDecimal.ZERO;

    @Builder.Default @Column(name = "delay_fee", nullable = false, precision = 23, scale = 3)
    private BigDecimal delayFee = BigDecimal.ZERO;

    @Column(name = "delayed_by", length = 20)
    private String delayedBy;

    @Builder.Default @Column(name = "vat_tax", nullable = false, precision = 23, scale = 3)
    private BigDecimal vatTax = BigDecimal.ZERO;

    @Builder.Default @Column(name = "tips", nullable = false, precision = 23, scale = 3)
    private BigDecimal tips = BigDecimal.ZERO;

    @Builder.Default @Column(name = "admin_commission", nullable = false, precision = 23, scale = 3)
    private BigDecimal adminCommission = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
