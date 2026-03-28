package com.drivemond.vehicle.entity;

import com.drivemond.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleCategory extends BaseEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String image;

    /** e.g. "car", "motor_bike" */
    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
