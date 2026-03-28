package com.drivedash.trip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trip_status")
@EntityListeners(AuditingEntityListener.class)
public class TripStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_request_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID tripRequestId;

    @Column(name = "customer_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID customerId;

    @Column(name = "driver_id", columnDefinition = "CHAR(36)")
    private UUID driverId;

    @Column(name = "pending") private LocalDateTime pending;
    @Column(name = "accepted") private LocalDateTime accepted;
    @Column(name = "out_for_pickup") private LocalDateTime outForPickup;
    @Column(name = "picked_up") private LocalDateTime pickedUp;
    @Column(name = "ongoing") private LocalDateTime ongoing;
    @Column(name = "completed") private LocalDateTime completed;
    @Column(name = "cancelled") private LocalDateTime cancelled;
    @Column(name = "failed") private LocalDateTime failed;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
