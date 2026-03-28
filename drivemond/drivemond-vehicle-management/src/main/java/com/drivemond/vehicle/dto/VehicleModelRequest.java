package com.drivemond.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class VehicleModelRequest {

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotNull
    private UUID brandId;

    private String description;

    @Positive
    private Integer seatCapacity;

    @Positive
    private Double maximumWeight;

    @Positive
    private Integer hatchBagCapacity;

    @NotBlank
    private String engine;

    private MultipartFile image;
}
