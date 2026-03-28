package com.drivemond.faremanagement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParcelFareSetupRequest {

    @NotNull @DecimalMin("0")
    private Double baseFare;

    @NotNull @DecimalMin("0")
    private Double baseFarePerKm;

    @NotNull @DecimalMin("0")
    private Double cancellationFeePercent;

    @NotNull @DecimalMin("0")
    private Double minCancellationFee;
}
