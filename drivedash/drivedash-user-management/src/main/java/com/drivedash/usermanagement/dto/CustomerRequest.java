package com.drivedash.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @Email
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Invalid phone number")
    private String phone;

    private String password;

    private UUID userLevelId;

    private boolean active = true;
}
