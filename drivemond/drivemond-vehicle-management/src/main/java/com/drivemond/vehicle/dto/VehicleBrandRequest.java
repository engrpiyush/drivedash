package com.drivemond.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VehicleBrandRequest {

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotBlank
    private String description;

    private MultipartFile image;
}
