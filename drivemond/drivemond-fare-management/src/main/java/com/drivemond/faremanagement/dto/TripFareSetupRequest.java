package com.drivemond.faremanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripFareSetupRequest {

    // ── Zone-wide defaults ────────────────────────────────────────────────────

    @NotNull @DecimalMin("0")
    private Double baseFare;

    @NotNull @DecimalMin("0")
    private Double baseFarePerKm;

    @NotNull @DecimalMin("0")
    private Double waitingFeePerMin;

    @NotNull @DecimalMin("0")
    private Double cancellationFeePercent;

    @NotNull @DecimalMin("0")
    private Double minCancellationFee;

    @NotNull @DecimalMin("0")
    private Double idleFeePerMin;

    @NotNull @DecimalMin("0")
    private Double tripDelayFeePerMin;

    @NotNull @DecimalMin("0")
    private Double penaltyFeeForCancel;

    @NotNull @DecimalMin("0")
    private Double feeAddToNext;

    /** true = per-category fares are active; false = use zone defaults for all */
    private boolean categoryWiseFare;

    /** Per-category fare overrides, populated only when categoryWiseFare=true */
    private List<CategoryFareRequest> categoryFares;
}
