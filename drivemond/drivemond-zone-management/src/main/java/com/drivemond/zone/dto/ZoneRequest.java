package com.drivemond.zone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ZoneRequest {

    @NotBlank(message = "Zone name is required")
    @Size(max = 191, message = "Zone name must not exceed 191 characters")
    private String name;

    private String coordinates; // raw "(lat, lng),(lat, lng),..." from Google Maps drawing
}
