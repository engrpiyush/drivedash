package com.drivemond.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BannerSetupRequest {

    @NotBlank(message = "Banner name is required")
    private String name;

    private String description;

    private boolean allTime;

    private String timePeriod;

    private String displayPosition;

    @NotBlank(message = "Redirect link is required")
    private String redirectLink;

    private String bannerGroup;

    private LocalDate startDate;

    private LocalDate endDate;

    private MultipartFile imageFile;
}
