package com.drivemond.review.entity;

import com.drivemond.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
@SQLDelete(sql = "UPDATE reviews SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_request_id", columnDefinition = "CHAR(36)")
    private UUID tripRequestId;

    @Column(name = "given_by", columnDefinition = "CHAR(36)")
    private UUID givenBy;

    @Column(name = "received_by", columnDefinition = "CHAR(36)")
    private UUID receivedBy;

    @Column(name = "trip_type", length = 30)
    private String tripType;

    @Builder.Default
    @Column(name = "rating", nullable = false, columnDefinition = "TINYINT")
    private int rating = 1;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "JSON")
    private List<String> images;

    @Builder.Default
    @Column(name = "is_saved", nullable = false)
    private boolean isSaved = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "given_by", insertable = false, updatable = false)
    private User givenUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by", insertable = false, updatable = false)
    private User receivedUser;
}
