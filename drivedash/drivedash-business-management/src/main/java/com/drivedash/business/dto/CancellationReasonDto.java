package com.drivedash.business.dto;

import com.drivedash.business.entity.CancellationType;
import com.drivedash.business.entity.CancellationUserType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/** Read-only projection of a {@link com.drivedash.business.entity.CancellationReason}. */
@Getter
@Builder
public class CancellationReasonDto {

    private UUID id;
    private String title;
    private CancellationType cancellationType;
    private CancellationUserType userType;
    private boolean active;
}