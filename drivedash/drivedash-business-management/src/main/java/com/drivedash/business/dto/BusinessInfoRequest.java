package com.drivedash.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form payload for the Business Information setup page.
 * Maps to the fields saved under {@code settings_type = BUSINESS_INFORMATION}.
 */
@Getter
@Setter
public class BusinessInfoRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    private String businessAddress;
    private String businessPhone;
    private String businessEmail;
    private String businessWebsite;
    private String currencyCode;
    private String currencySymbolPosition;
    private Integer currencyDecimalPoint;
    private String timeFormat;
    private String copyrightText;
    private String countryCode;

    // Toggle flags (absent in form = 0)
    private boolean driverVerification;
    private boolean customerVerification;
    private boolean emailVerification;
    private boolean driverSelfRegistration;
    private boolean otpVerification;

    // File uploads
    private MultipartFile headerLogo;
    private MultipartFile favicon;
    private MultipartFile preloader;
}