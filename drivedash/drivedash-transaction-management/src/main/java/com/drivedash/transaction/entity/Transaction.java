package com.drivedash.transaction.entity;

import com.drivedash.auth.entity.User;
import com.drivedash.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
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
@Table(name = "transactions")
public class Transaction extends BaseAuditEntity {

    @Column(name = "attribute_id", columnDefinition = "CHAR(36)")
    private UUID attributeId;

    @Column(name = "attribute", length = 191)
    private String attribute;

    @Builder.Default
    @Column(name = "debit", nullable = false, precision = 24, scale = 2)
    private BigDecimal debit = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "credit", nullable = false, precision = 24, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "balance", nullable = false, precision = 24, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "user_id", columnDefinition = "CHAR(36)")
    private UUID userId;

    @Column(name = "account", length = 191)
    private String account;

    @Column(name = "trx_ref_id", columnDefinition = "CHAR(36)")
    private UUID trxRefId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
