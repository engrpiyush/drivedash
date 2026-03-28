package com.drivedash.business.repository;

import com.drivedash.business.entity.PaymentRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, UUID> {

    Page<PaymentRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<PaymentRequest> findByUserId(UUID userId, Pageable pageable);

    Page<PaymentRequest> findByPaymentPlatform(String platform, Pageable pageable);

    List<PaymentRequest> findByUserIdAndIsPaidTrue(UUID userId);

    Optional<PaymentRequest> findByIdAndUserId(UUID id, UUID userId);

    long countByIsPaidTrue();

    long countByIsPaidFalse();
}
