package com.drivemond.business.dto;

import com.drivemond.business.entity.CancellationType;
import com.drivemond.business.entity.CancellationUserType;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/** Read-only projection of a {@link com.drivemond.business.entity.CancellationReason}. */
@Getter
@Builder
public class CancellationReasonDto {

    private UUID id;
    private String title;
    private CancellationType cancellationType;
    private CancellationUserType userType;
    private boolean active;
}