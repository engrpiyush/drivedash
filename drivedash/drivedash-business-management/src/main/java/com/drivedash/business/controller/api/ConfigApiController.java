package com.drivedash.business.controller.api;

import com.drivedash.business.dto.CancellationReasonDto;
import com.drivedash.business.entity.CancellationUserType;
import com.drivedash.business.entity.SettingsType;
import com.drivedash.business.service.BusinessSettingService;
import com.drivedash.business.service.CancellationReasonService;
import com.drivedash.core.response.ApiResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public configuration API consumed by mobile apps (driver & customer).
 * Mirrors Laravel's {@code Api\Customer\ConfigController} and
 * {@code Api\Driver\ConfigController}.
 *
 * <p>Routes: /api/v1/config/**
 */
@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigApiController {

    private final BusinessSettingService businessSettingService;
    private final CancellationReasonService cancellationReasonService;

    /**
     * Returns all app configuration for the mobile customer app.
     * Equivalent to GET /customer/configuration.
     */
    @GetMapping("/customer/configuration")
    public ResponseEntity<ApiResponse<Map<String, Object>>> customerConfiguration() {
        Map<String, Object> config = Map.of(
                "business_info",    businessSettingService.findAllByType(SettingsType.BUSINESS_INFORMATION),
                "business_settings", businessSettingService.findAllByType(SettingsType.BUSINESS_SETTINGS),
                "customer_settings", businessSettingService.findAllByType(SettingsType.CUSTOMER_SETTINGS),
                "trip_settings",    businessSettingService.findAllByType(SettingsType.TRIP_SETTINGS),
                "payment_config",   businessSettingService.getValue("payment_config", SettingsType.PAYMENT_CONFIG)
        );
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    /**
     * Returns all app configuration for the mobile driver app.
     * Equivalent to GET /driver/configuration.
     */
    @GetMapping("/driver/configuration")
    public ResponseEntity<ApiResponse<Map<String, Object>>> driverConfiguration() {
        Map<String, Object> config = Map.of(
                "business_info",   businessSettingService.findAllByType(SettingsType.BUSINESS_INFORMATION),
                "driver_settings", businessSettingService.findAllByType(SettingsType.DRIVER_SETTINGS),
                "trip_settings",   businessSettingService.findAllByType(SettingsType.TRIP_SETTINGS)
        );
        return ResponseEntity.ok(ApiResponse.success(config));
    }

    /**
     * Returns active cancellation reasons filtered by user type.
     * Equivalent to GET /customer/config/cancellation-reason-list
     * and GET /driver/config/cancellation-reason-list.
     */
    @GetMapping("/cancellation-reason-list")
    public ResponseEntity<ApiResponse<List<CancellationReasonDto>>> cancellationReasonList(
            @RequestParam(defaultValue = "CUSTOMER") CancellationUserType userType) {
        List<CancellationReasonDto> reasons =
                cancellationReasonService.findAllByUserType(userType);
        return ResponseEntity.ok(ApiResponse.success(reasons));
    }
}