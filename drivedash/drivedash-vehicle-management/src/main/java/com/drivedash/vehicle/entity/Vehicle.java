package com.drivedash.vehicle.entity;

import com.drivedash.auth.entity.User;
import com.drivedash.core.entity.BaseEntity;
import com.drivedash.vehicle.enums.FuelType;
import com.drivedash.vehicle.enums.Ownership;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle extends BaseEntity {

    @Column(name = "ref_id", length = 20)
    private String refId;

    @Column(name = "brand_id", nullable = false)
    private UUID brandId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", insertable = false, updatable = false)
    private VehicleBrand brand;

    @Column(name = "model_id", nullable = false)
    private UUID modelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    private VehicleModel model;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private VehicleCategory category;

    @Column(name = "licence_plate_number", nullable = false, length = 50)
    private String licencePlateNumber;

    @Column(name = "licence_expire_date")
    private LocalDate licenceExpireDate;

    @Column(name = "vin_number", length = 100)
    private String vinNumber;

    @Column(length = 100)
    private String transmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", columnDefinition = "VARCHAR(20)")
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership", columnDefinition = "VARCHAR(10)")
    private Ownership ownership;

    @Column(name = "driver_id")
    private UUID driverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", insertable = false, updatable = false)
    private User driver;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    @Builder.Default
    private List<String> documents = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = false;
}
