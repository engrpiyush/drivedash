package com.drivedash.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class UserLevelRequest {

    @NotNull @Positive
    private Integer sequence;

    @NotBlank
    private String name;

    @NotBlank
    private String userType;

    private String rewardType = "point";

    private BigDecimal rewardAmount;

    private MultipartFile imageFile;

    private int targetedRide;
    private int targetedRidePoint;
    private double targetedAmount;
    private int targetedAmountPoint;
    private int targetedCancel;
    private int targetedCancelPoint;
    private int targetedReview;
    private int targetedReviewPoint;

    // level_accesses
    private boolean bid;
    private boolean seeDestination;
    private boolean seeSubtotal;
    private boolean seeLevel;
    private boolean createHireRequest;
}
