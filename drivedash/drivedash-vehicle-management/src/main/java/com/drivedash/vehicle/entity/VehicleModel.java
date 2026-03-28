package com.drivedash.vehicle.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vehicle_models",
       uniqueConstraints = @UniqueConstraint(name = "uq_model_name_brand", columnNames = {"name", "brand_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleModel extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "brand_id", nullable = false)
    private UUID brandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private VehicleBrand brand;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    @Column(name = "maximum_weight", columnDefinition = "DECIMAL(10,2)")
    private Double maximumWeight;

    @Column(name = "hatch_bag_capacity")
    private Integer hatchBagCapacity;

    @Column(length = 100)
    private String engine;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String image;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
