package com.drivemond.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/** Lightweight read projection of an {@link com.drivemond.admin.entity.AdminNotification}. */
@Getter
@Builder
public class NotificationDto {

    private Long id;
    private String model;
    private UUID modelId;
    private String message;
    private boolean seen;
    private LocalDateTime createdAt;
}
