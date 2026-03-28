package com.drivedash.parcel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParcelWeightRequest {

    @NotNull @DecimalMin("0")
    private Double minWeight;

    @NotNull @DecimalMin("0")
    private Double maxWeight;
}
