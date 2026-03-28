package com.drivedash.usermanagement.entity;

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
@Table(name = "level_accesses")
@EntityListeners(AuditingEntityListener.class)
public class LevelAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "level_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID levelId;

    @Column(name = "user_type", nullable = false, length = 50)
    private String userType;

    @Builder.Default @Column(name = "bid", nullable = false) private boolean bid = false;
    @Builder.Default @Column(name = "see_destination", nullable = false) private boolean seeDestination = false;
    @Builder.Default @Column(name = "see_subtotal", nullable = false) private boolean seeSubtotal = false;
    @Builder.Default @Column(name = "see_level", nullable = false) private boolean seeLevel = false;
    @Builder.Default @Column(name = "create_hire_request", nullable = false) private boolean createHireRequest = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
