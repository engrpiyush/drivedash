package com.drivemond.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VehicleCategoryRequest {

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String type; // car, motor_bike

    private MultipartFile image;
}
