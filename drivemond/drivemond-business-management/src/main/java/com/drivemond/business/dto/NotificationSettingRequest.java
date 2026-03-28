package com.drivemond.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Request body for toggling push/email on a notification event. */
@Getter
@Setter
public class NotificationSettingRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private boolean push;
    private boolean email;
}