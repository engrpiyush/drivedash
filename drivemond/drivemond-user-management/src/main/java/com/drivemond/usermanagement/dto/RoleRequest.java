package com.drivemond.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {

    @NotBlank
    private String name;

    /** Module permission keys, e.g. ["customer", "driver", "zone"] */
    private List<String> modules;

    private boolean active = true;
}
