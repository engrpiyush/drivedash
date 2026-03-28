package com.drivedash.parcel.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parcel_categories")
public class ParcelCategory extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 191)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", nullable = false, length = 191)
    private String image;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
