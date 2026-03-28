package com.drivedash.faremanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryFareRequest {

    private UUID categoryId;
    private double baseFare;
    private double baseFarePerKm;
    private double waitingFeePerMin;
    private double cancellationFeePercent;
    private double minCancellationFee;
    private double idleFeePerMin;
    private double tripDelayFeePerMin;
    private double penaltyFeeForCancel;
    private double feeAddToNext;
}
