package com.drivemond.zone.entity;

import com.drivemond.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Polygon;

@Entity
@Table(name = "zones", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Zone extends BaseEntity {

    @Column(nullable = false, unique = true, length = 191)
    private String name;

    @Column(columnDefinition = "geometry")
    private Polygon coordinates;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
