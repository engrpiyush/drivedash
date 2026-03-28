package com.drivemond.trip.entity;

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
@Table(name = "trip_request_times")
@EntityListeners(AuditingEntityListener.class)
public class TripRequestTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_request_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID tripRequestId;

    @Builder.Default
    @Column(name = "estimated_time", nullable = false)
    private double estimatedTime = 0;

    @Column(name = "actual_time")  private Double actualTime;
    @Column(name = "waiting_time") private Double waitingTime;
    @Column(name = "delay_time")   private Double delayTime;

    @Column(name = "idle_timestamp") private LocalDateTime idleTimestamp;

    @Column(name = "idle_time")           private Double idleTime;
    @Column(name = "driver_arrival_time") private Double driverArrivalTime;

    @Column(name = "driver_arrival_timestamp") private LocalDateTime driverArrivalTimestamp;
    @Column(name = "driver_arrives_at")        private LocalDateTime driverArrivesAt;
    @Column(name = "customer_arrives_at")      private LocalDateTime customerArrivesAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
