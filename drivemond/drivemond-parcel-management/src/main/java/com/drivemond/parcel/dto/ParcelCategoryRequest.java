package com.drivemond.parcel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ParcelCategoryRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private MultipartFile imageFile;
}
