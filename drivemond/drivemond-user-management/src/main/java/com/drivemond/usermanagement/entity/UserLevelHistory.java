package com.drivemond.usermanagement.entity;

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
@Table(name = "user_level_histories")
@EntityListeners(AuditingEntityListener.class)
public class UserLevelHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_level_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userLevelId;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userId;

    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;

    @Builder.Default @Column(name = "completed_ride", nullable = false) private int completedRide = 0;
    @Builder.Default @Column(name = "ride_reward_status", nullable = false) private boolean rideRewardStatus = false;

    @Builder.Default @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Builder.Default @Column(name = "amount_reward_status", nullable = false) private boolean amountRewardStatus = false;

    @Builder.Default @Column(name = "cancellation_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal cancellationRate = BigDecimal.ZERO;

    @Builder.Default @Column(name = "cancellation_reward_status", nullable = false) private boolean cancellationRewardStatus = false;
    @Builder.Default @Column(name = "reviews", nullable = false) private int reviews = 0;
    @Builder.Default @Column(name = "reviews_reward_status", nullable = false) private boolean reviewsRewardStatus = false;
    @Builder.Default @Column(name = "is_level_reward_granted", nullable = false) private boolean levelRewardGranted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
