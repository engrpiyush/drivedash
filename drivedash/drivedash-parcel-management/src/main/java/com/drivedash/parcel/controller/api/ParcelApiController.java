package com.drivedash.parcel.controller.api;

import com.drivedash.parcel.entity.ParcelCategory;
import com.drivedash.parcel.entity.ParcelWeight;
import com.drivedash.parcel.service.ParcelCategoryService;
import com.drivedash.parcel.service.ParcelWeightService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parcel")
@RequiredArgsConstructor
public class ParcelApiController {

    private final ParcelCategoryService categoryService;
    private final ParcelWeightService weightService;

    @GetMapping("/categories")
    public ResponseEntity<List<ParcelCategory>> getCategories() {
        return ResponseEntity.ok(categoryService.getActiveList());
    }

    @GetMapping("/weights")
    public ResponseEntity<List<ParcelWeight>> getWeights() {
        return ResponseEntity.ok(weightService.getActiveList());
    }
}
