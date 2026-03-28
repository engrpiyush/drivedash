package com.drivemond.auth.dto;

import com.drivemond.auth.entity.UserType;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Lightweight user projection returned in auth responses.
 * Only exposes fields safe to send to clients.
 */
@Getter
@Builder
public class UserSummaryDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profileImage;
    private UserType userType;
    private Set<String> roles;
}
