package com.drivedash.vehicle.dto;

import com.drivedash.vehicle.enums.FuelType;
import com.drivedash.vehicle.enums.Ownership;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class VehicleRequest {

    @NotNull
    private UUID brandId;

    @NotNull
    private UUID modelId;

    @NotNull
    private UUID categoryId;

    @NotNull
    private String licencePlateNumber;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate licenceExpireDate;

    private String vinNumber;
    private String transmission;

    @NotNull
    private FuelType fuelType;

    @NotNull
    private Ownership ownership;

    @NotNull
    private UUID driverId;

    private List<MultipartFile> documents;
}
