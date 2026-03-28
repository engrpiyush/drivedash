package com.drivedash.parcel.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "parcel_weights")
public class ParcelWeight extends BaseEntity {

    @Builder.Default
    @Column(name = "min_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal minWeight = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "max_weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxWeight = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
