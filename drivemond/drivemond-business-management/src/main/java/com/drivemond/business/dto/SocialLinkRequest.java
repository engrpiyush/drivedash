package com.drivemond.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Request body for creating or updating a {@link com.drivemond.business.entity.SocialLink}. */
@Getter
@Setter
public class SocialLinkRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Link is required")
    private String link;

    private boolean active = true;
}