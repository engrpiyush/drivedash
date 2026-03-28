package com.drivedash.promotion.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "banner_setups")
@SQLDelete(sql = "UPDATE banner_setups SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class BannerSetup extends BaseEntity {

    @Column(name = "name", nullable = false, length = 191)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "time_period", length = 191)
    private String timePeriod;

    @Column(name = "display_position", length = 191)
    private String displayPosition;

    @Column(name = "redirect_link", length = 500)
    private String redirectLink;

    @Column(name = "banner_group", length = 191)
    private String bannerGroup;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "image", length = 500)
    private String image;

    @Builder.Default
    @Column(name = "total_redirection", nullable = false, precision = 24, scale = 2)
    private BigDecimal totalRedirection = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
