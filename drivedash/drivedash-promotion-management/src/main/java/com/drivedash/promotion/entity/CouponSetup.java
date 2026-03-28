package com.drivedash.promotion.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_setups")
@SQLDelete(sql = "UPDATE coupon_setups SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class CouponSetup extends BaseEntity {

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private UUID userId;

    @Column(name = "user_level_id", columnDefinition = "CHAR(36)")
    private UUID userLevelId;

    @Builder.Default
    @Column(name = "min_trip_amount", nullable = false, precision = 24, scale = 2)
    private BigDecimal minTripAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "max_coupon_amount", nullable = false, precision = 24, scale = 2)
    private BigDecimal maxCouponAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "coupon", nullable = false, precision = 24, scale = 2)
    private BigDecimal coupon = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "amount_type", nullable = false, length = 15)
    private String amountType = "percentage";

    @Builder.Default
    @Column(name = "coupon_type", nullable = false, length = 15)
    private String couponType = "default";

    @Column(name = "coupon_code", length = 30, unique = true)
    private String couponCode;

    @Column(name = "`limit`")
    private Integer limit;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "rules", length = 191)
    private String rules = "default";

    @Builder.Default
    @Column(name = "total_used", nullable = false, precision = 24, scale = 2)
    private BigDecimal totalUsed = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_amount", nullable = false, precision = 24, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "coupon_setup_vehicle_category",
            joinColumns = @JoinColumn(name = "coupon_setup_id"))
    @Column(name = "vehicle_category_id", columnDefinition = "CHAR(36)")
    private List<UUID> vehicleCategoryIds = new ArrayList<>();
}
