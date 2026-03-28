package com.drivemond.business.dto;

import com.drivemond.business.entity.CancellationType;
import com.drivemond.business.entity.CancellationUserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for creating or updating a {@link com.drivemond.business.entity.CancellationReason}.
 */
@Getter
@Setter
public class CancellationReasonRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Cancellation type is required")
    private CancellationType cancellationType;

    @NotNull(message = "User type is required")
    private CancellationUserType userType;

    private boolean active = true;
}