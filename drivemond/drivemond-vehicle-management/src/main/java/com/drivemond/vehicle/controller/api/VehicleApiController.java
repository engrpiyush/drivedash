package com.drivemond.vehicle.controller.api;

import com.drivemond.core.response.ApiResponse;
import com.drivemond.vehicle.entity.VehicleBrand;
import com.drivemond.vehicle.entity.VehicleCategory;
import com.drivemond.vehicle.entity.VehicleModel;
import com.drivemond.vehicle.service.VehicleBrandService;
import com.drivemond.vehicle.service.VehicleCategoryService;
import com.drivemond.vehicle.service.VehicleModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle")
@RequiredArgsConstructor
public class VehicleApiController {

    private final VehicleBrandService brandService;
    private final VehicleCategoryService categoryService;
    private final VehicleModelService modelService;

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<VehicleBrand>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getActiveList()));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<VehicleCategory>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getActiveList()));
    }

    @GetMapping("/models")
    public ResponseEntity<ApiResponse<List<VehicleModel>>> getModels(
            @RequestParam(required = false) UUID brandId) {
        List<VehicleModel> models = brandId != null
                ? modelService.getActiveByBrand(brandId)
                : modelService.getAllActive();
        return ResponseEntity.ok(ApiResponse.success(models));
    }
}
