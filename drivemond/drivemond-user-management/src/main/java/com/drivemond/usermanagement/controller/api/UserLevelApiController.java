package com.drivemond.usermanagement.controller.api;

import com.drivemond.core.response.ApiResponse;
import com.drivemond.usermanagement.entity.UserLevel;
import com.drivemond.usermanagement.repository.WithdrawMethodRepository;
import com.drivemond.usermanagement.service.UserLevelService;
import com.drivemond.usermanagement.service.WithdrawService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserLevelApiController {

    private final UserLevelService levelService;
    private final WithdrawService withdrawService;

    @GetMapping("/customer/levels")
    public ResponseEntity<ApiResponse<List<UserLevel>>> customerLevels() {
        return ResponseEntity.ok(ApiResponse.success(levelService.getActiveByType("customer")));
    }

    @GetMapping("/driver/levels")
    public ResponseEntity<ApiResponse<List<UserLevel>>> driverLevels() {
        return ResponseEntity.ok(ApiResponse.success(levelService.getActiveByType("driver")));
    }

    @GetMapping("/driver/withdraw/methods")
    public ResponseEntity<ApiResponse<?>> withdrawMethods() {
        return ResponseEntity.ok(ApiResponse.success(withdrawService.getActiveMethods()));
    }
}
