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
@Table(name = "recent_addresses")
@EntityListeners(AuditingEntityListener.class)
public class RecentAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private UUID userId;

    @Column(name = "zone_id", columnDefinition = "CHAR(36)")
    private UUID zoneId;

    @Column(name = "pickup_lat")     private Double pickupLat;
    @Column(name = "pickup_lng")     private Double pickupLng;
    @Column(name = "pickup_address", length = 500) private String pickupAddress;

    @Column(name = "destination_lat") private Double destinationLat;
    @Column(name = "destination_lng") private Double destinationLng;
    @Column(name = "destination_address", length = 500) private String destinationAddress;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
