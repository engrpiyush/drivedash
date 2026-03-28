package com.drivemond.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawMethodRequest {

    @NotBlank
    private String methodName;

    /** Raw JSON string from textarea representing the fields array */
    private String methodFieldsJson;

    private boolean isDefault;

    private boolean active = true;
}
