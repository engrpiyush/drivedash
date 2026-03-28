package com.drivedash.usermanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
@Table(name = "driver_details")
@EntityListeners(AuditingEntityListener.class)
public class DriverDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userId;

    @Builder.Default
    @Column(name = "is_online", nullable = false)
    private boolean online = false;

    @Builder.Default
    @Column(name = "availability_status", nullable = false, length = 30)
    private String availabilityStatus = "unavailable";

    @Column(name = "online") private LocalTime onlineTime;
    @Column(name = "offline") private LocalTime offlineTime;

    @Builder.Default
    @Column(name = "online_time", nullable = false)
    private double totalOnlineTime = 0;

    @Column(name = "accepted") private LocalTime accepted;
    @Column(name = "completed") private LocalTime completed;
    @Column(name = "start_driving") private LocalTime startDriving;

    @Builder.Default
    @Column(name = "on_driving_time", nullable = false)
    private double onDrivingTime = 0;

    @Builder.Default
    @Column(name = "idle_time", nullable = false)
    private double idleTime = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
