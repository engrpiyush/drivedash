package com.drivedash.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Query parameters for the activity log list page.
 * Maps to Laravel's {@code $request->all()} passed into
 * {@code ActivityLogService::log()}.
 */
@Getter
@Setter
public class ActivityLogFilterRequest {

    @NotBlank(message = "logable_type is required")
    private String logableType;

    private UUID logableId;

    private String userType;

    private int page = 0;

    private int size = 15;
}
