package com.drivemond.business.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for {@code POST /api/v1/payment/requests}.
 * Sent by the mobile app to create a pending payment record before the user
 * interacts with the gateway SDK on-device.
 */
@Getter
@Setter
public class PaymentInitRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String gatewaySlug;

    /** {@code wallet}, {@code trip_request}, {@code parcel}, etc. */
    @NotBlank
    private String attribute;

    private UUID attributeId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String currencyCode = "USD";

    private String paymentMethod;
}
