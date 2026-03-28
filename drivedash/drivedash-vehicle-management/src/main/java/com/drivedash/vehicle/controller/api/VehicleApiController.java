package com.drivedash.vehicle.controller.api;

import com.drivedash.core.response.ApiResponse;
import com.drivedash.vehicle.entity.VehicleBrand;
import com.drivedash.vehicle.entity.VehicleCategory;
import com.drivedash.vehicle.entity.VehicleModel;
import com.drivedash.vehicle.service.VehicleBrandService;
import com.drivedash.vehicle.service.VehicleCategoryService;
import com.drivedash.vehicle.service.VehicleModelService;
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
