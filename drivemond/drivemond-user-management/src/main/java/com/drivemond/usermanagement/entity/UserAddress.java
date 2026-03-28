package com.drivemond.usermanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_addresses")
@EntityListeners(AuditingEntityListener.class)
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private UUID userId;

    @Column(name = "zone_id", columnDefinition = "CHAR(36)")
    private UUID zoneId;

    @Column(name = "latitude", length = 191)
    private String latitude;

    @Column(name = "longitude", length = 191)
    private String longitude;

    @Column(name = "city", length = 191)
    private String city;

    @Column(name = "street", length = 191)
    private String street;

    @Column(name = "house", length = 191)
    private String house;

    @Column(name = "zip_code", length = 50)
    private String zipCode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "contact_person_name", length = 191)
    private String contactPersonName;

    @Column(name = "contact_person_phone", length = 50)
    private String contactPersonPhone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "address_label", length = 100)
    private String addressLabel;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
