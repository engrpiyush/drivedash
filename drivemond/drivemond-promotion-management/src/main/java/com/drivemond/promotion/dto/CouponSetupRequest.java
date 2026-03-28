package com.drivemond.promotion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponSetupRequest {

    @NotBlank(message = "Coupon title is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Coupon code is required")
    private String couponCode;

    private UUID userId;

    private UUID userLevelId;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal coupon;

    @NotBlank(message = "Amount type is required")
    private String amountType;

    @NotBlank(message = "Coupon type is required")
    private String couponType;

    @NotNull(message = "Usage limit is required")
    @Min(value = 1, message = "Limit must be at least 1")
    private Integer limit;

    @NotNull(message = "Minimum trip amount is required")
    @DecimalMin(value = "0.01", message = "Minimum trip amount must be greater than 0")
    private BigDecimal minTripAmount;

    private BigDecimal maxCouponAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Rules are required")
    private String rules;

    private List<UUID> vehicleCategoryIds = new ArrayList<>();
}
