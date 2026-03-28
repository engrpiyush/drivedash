package com.drivedash.business.service;

import com.drivedash.business.dto.PaymentInitRequest;
import com.drivedash.business.entity.PaymentRequest;
import com.drivedash.business.repository.PaymentRequestRepository;
import com.drivedash.core.exception.DrivedashException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages {@link PaymentRequest} lifecycle: creation, completion, and admin queries.
 *
 * <p>Actual gateway SDK calls are handled by the mobile client; this service
 * only persists the before/after state of each payment attempt.
 */
@Service
@RequiredArgsConstructor
public class PaymentRequestService {

    private final PaymentRequestRepository repository;

    // ── Reads ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<PaymentRequest> getPage(int page, int size) {
        return repository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public Page<PaymentRequest> getByUser(UUID userId, int page, int size) {
        return repository.findByUserId(userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public Page<PaymentRequest> getByPlatform(String platform, int page, int size) {
        return repository.findByPaymentPlatform(platform,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    // ── Writes ───────────────────────────────────────────────────────────────

    /**
     * Creates a pending {@link PaymentRequest} before the user interacts with
     * the gateway SDK. Returns the persisted entity so the client can store its ID.
     */
    @Transactional
    public PaymentRequest create(PaymentInitRequest req) {
        PaymentRequest pr = PaymentRequest.builder()
                .userId(req.getUserId())
                .paymentPlatform(req.getGatewaySlug())
                .attribute(req.getAttribute())
                .attributeId(req.getAttributeId())
                .amount(req.getAmount())
                .currencyCode(req.getCurrencyCode() != null ? req.getCurrencyCode() : "USD")
                .paymentMethod(req.getPaymentMethod())
                .isPaid(false)
                .build();
        return repository.save(pr);
    }

    /**
     * Marks a payment as completed after the mobile client confirms success with
     * the gateway. Stores the gateway-side intent/order ID for reconciliation.
     */
    @Transactional
    public PaymentRequest markPaid(UUID id, UUID userId, String externalIntentId) {
        PaymentRequest pr = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> DrivedashException.notFound("Payment request not found"));

        if (pr.isPaid()) {
            throw DrivedashException.conflict("Payment already completed");
        }

        pr.setExternalIntentId(externalIntentId);
        pr.setPaid(true);
        return repository.save(pr);
    }

    /**
     * Attaches the internal transaction reference after the hook writes to the
     * {@code transactions} table (called from the trip/parcel services).
     */
    @Transactional
    public void linkTransaction(UUID paymentRequestId, UUID trxRefId) {
        repository.findById(paymentRequestId).ifPresent(pr -> {
            pr.setTrxRefId(trxRefId);
            repository.save(pr);
        });
    }
}
