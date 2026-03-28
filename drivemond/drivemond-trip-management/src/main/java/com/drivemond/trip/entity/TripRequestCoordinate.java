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
@Table(name = "trip_request_coordinates")
@EntityListeners(AuditingEntityListener.class)
public class TripRequestCoordinate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_request_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID tripRequestId;

    @Column(name = "pickup_lat")     private Double pickupLat;
    @Column(name = "pickup_lng")     private Double pickupLng;
    @Column(name = "pickup_address", length = 500) private String pickupAddress;

    @Column(name = "destination_lat") private Double destinationLat;
    @Column(name = "destination_lng") private Double destinationLng;
    @Column(name = "destination_address", length = 500) private String destinationAddress;

    @Builder.Default
    @Column(name = "is_reached_destination", nullable = false)
    private boolean isReachedDestination = false;

    @Column(name = "intermediate_coordinates", columnDefinition = "TEXT")
    private String intermediateCoordinates;

    @Column(name = "int_lat_1") private Double intLat1;
    @Column(name = "int_lng_1") private Double intLng1;

    @Builder.Default
    @Column(name = "is_reached_1", nullable = false)
    private boolean isReached1 = false;

    @Column(name = "int_lat_2") private Double intLat2;
    @Column(name = "int_lng_2") private Double intLng2;

    @Builder.Default
    @Column(name = "is_reached_2", nullable = false)
    private boolean isReached2 = false;

    @Column(name = "intermediate_addresses", columnDefinition = "TEXT")
    private String intermediateAddresses;

    @Column(name = "start_lat")            private Double startLat;
    @Column(name = "start_lng")            private Double startLng;
    @Column(name = "drop_lat")             private Double dropLat;
    @Column(name = "drop_lng")             private Double dropLng;
    @Column(name = "driver_accept_lat")    private Double driverAcceptLat;
    @Column(name = "driver_accept_lng")    private Double driverAcceptLng;
    @Column(name = "customer_request_lat") private Double customerRequestLat;
    @Column(name = "customer_request_lng") private Double customerRequestLng;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
