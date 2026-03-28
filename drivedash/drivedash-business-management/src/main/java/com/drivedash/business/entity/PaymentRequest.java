package com.drivedash.business.entity;

import com.drivedash.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Records every payment attempt made through any configured gateway.
 *
 * <p>Gateway credentials are stored separately in {@code business_settings}
 * with {@code settings_type = 'PAYMENT_CONFIG'}; this table only tracks
 * individual payment transactions.
 *
 * <p>Mirrors {@code Modules/Payment/Entities/PaymentRequest} from the PHP source.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_requests")
public class PaymentRequest extends BaseAuditEntity {

    /** UUID of the paying user (customer or driver). */
    @Column(name = "user_id", columnDefinition = "CHAR(36)", nullable = false)
    private UUID userId;

    /** Gateway slug, e.g. {@code stripe}, {@code paypal}, {@code razorpay}. */
    @Column(name = "payment_platform", length = 100, nullable = false)
    private String paymentPlatform;

    /**
     * What this payment is for: {@code wallet}, {@code trip_request}, {@code parcel}, etc.
     * Mirrors the polymorphic {@code attribute} column.
     */
    @Column(name = "attribute", length = 191)
    private String attribute;

    /** UUID of the associated entity (trip ID, parcel ID, etc.). */
    @Column(name = "attribute_id", columnDefinition = "CHAR(36)")
    private UUID attributeId;

    /** Internal transaction reference — links to the {@code transactions} table. */
    @Column(name = "trx_ref_id", columnDefinition = "CHAR(36)")
    private UUID trxRefId;

    /** Gateway-side payer identifier (PayPal payer_id, Stripe customer id, etc.). */
    @Column(name = "payer_id", length = 191)
    private String payerId;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Builder.Default
    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "currency_code", length = 10, nullable = false)
    private String currencyCode = "USD";

    /**
     * Gateway-side order / payment-intent ID returned after initiation.
     * Used for verification on callback.
     */
    @Column(name = "external_intent_id", length = 255)
    private String externalIntentId;

    @Builder.Default
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;
}
