package com.drivedash.usermanagement.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "user_levels")
public class UserLevel extends BaseEntity {

    @Builder.Default
    @Column(name = "sequence", nullable = false)
    private int sequence = 0;

    @Column(name = "name", nullable = false, length = 191)
    private String name;

    @Builder.Default
    @Column(name = "reward_type", nullable = false, length = 20)
    private String rewardType = "point";

    @Column(name = "reward_amount", precision = 15, scale = 2)
    private BigDecimal rewardAmount;

    @Column(name = "image", length = 191)
    private String image;

    @Builder.Default @Column(name = "targeted_ride", nullable = false)
    private int targetedRide = 0;

    @Builder.Default @Column(name = "targeted_ride_point", nullable = false)
    private int targetedRidePoint = 0;

    @Builder.Default @Column(name = "targeted_amount", nullable = false)
    private double targetedAmount = 0;

    @Builder.Default @Column(name = "targeted_amount_point", nullable = false)
    private int targetedAmountPoint = 0;

    @Builder.Default @Column(name = "targeted_cancel", nullable = false)
    private int targetedCancel = 0;

    @Builder.Default @Column(name = "targeted_cancel_point", nullable = false)
    private int targetedCancelPoint = 0;

    @Builder.Default @Column(name = "targeted_review", nullable = false)
    private int targetedReview = 0;

    @Builder.Default @Column(name = "targeted_review_point", nullable = false)
    private int targetedReviewPoint = 0;

    /** "customer" or "driver" */
    @Column(name = "user_type", nullable = false, length = 20)
    private String userType;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
