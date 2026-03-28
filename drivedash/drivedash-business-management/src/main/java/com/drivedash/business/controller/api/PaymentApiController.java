package com.drivedash.business.controller.api;

import com.drivedash.business.dto.PaymentInitRequest;
import com.drivedash.business.entity.PaymentRequest;
import com.drivedash.business.service.GatewayConfigService;
import com.drivedash.business.service.PaymentRequestService;
import com.drivedash.core.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Payment API consumed by mobile apps.
 *
 * <p>Routes: /api/v1/payment/**
 *
 * <p>Actual gateway SDK calls are executed on-device; this controller only
 * provides active gateway metadata and persists the payment lifecycle.
 */
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentApiController {

    private final GatewayConfigService gatewayConfigService;
    private final PaymentRequestService paymentRequestService;

    /**
     * Returns all active gateways with public-safe credentials only.
     * Secret keys are never sent to mobile clients.
     *
     * <p>Equivalent to {@code Api\Customer\PaymentController@gateways}.
     */
    @GetMapping("/gateways")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> activeGateways() {
        return ResponseEntity.ok(
                ApiResponse.success(gatewayConfigService.getActiveGatewaysPublic()));
    }

    /**
     * Creates a pending payment request record before the user initiates payment
     * on-device via the gateway SDK.
     *
     * <p>Equivalent to {@code Api\Customer\PaymentController@initiate}.
     */
    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<PaymentRequest>> initiate(
            @RequestBody @Valid PaymentInitRequest req) {
        PaymentRequest pr = paymentRequestService.create(req);
        return ResponseEntity.ok(ApiResponse.success(pr));
    }

    /**
     * Called by the mobile app after the user completes payment in the gateway SDK.
     * Marks the request as paid and stores the gateway-side intent ID.
     *
     * <p>Equivalent to {@code Api\Customer\PaymentController@success}.
     */
    @PutMapping("/requests/{id}/complete")
    public ResponseEntity<ApiResponse<PaymentRequest>> complete(
            @PathVariable UUID id,
            @RequestParam UUID userId,
            @RequestParam String externalIntentId) {
        PaymentRequest pr = paymentRequestService.markPaid(id, userId, externalIntentId);
        return ResponseEntity.ok(ApiResponse.success(pr));
    }
}