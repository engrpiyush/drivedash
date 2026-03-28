package com.drivedash.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DriverRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Invalid phone number")
    private String phone;

    private String password;

    private String identificationNumber;

    private String identificationType;

    private MultipartFile profileImageFile;

    private UUID userLevelId;

    private boolean active = false;
}
