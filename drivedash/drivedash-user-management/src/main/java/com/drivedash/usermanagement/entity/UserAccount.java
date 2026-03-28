package com.drivedash.usermanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "user_accounts")
@EntityListeners(AuditingEntityListener.class)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private UUID userId;

    @Builder.Default @Column(name = "payable_balance", nullable = false, precision = 24, scale = 2)
    private BigDecimal payableBalance = BigDecimal.ZERO;

    @Builder.Default @Column(name = "receivable_balance", nullable = false, precision = 24, scale = 2)
    private BigDecimal receivableBalance = BigDecimal.ZERO;

    @Builder.Default @Column(name = "received_balance", nullable = false, precision = 24, scale = 2)
    private BigDecimal receivedBalance = BigDecimal.ZERO;

    @Builder.Default @Column(name = "pending_balance", nullable = false, precision = 24, scale = 2)
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    @Builder.Default @Column(name = "wallet_balance", nullable = false, precision = 24, scale = 2)
    private BigDecimal walletBalance = BigDecimal.ZERO;

    @Builder.Default @Column(name = "total_withdrawn", nullable = false, precision = 24, scale = 2)
    private BigDecimal totalWithdrawn = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
