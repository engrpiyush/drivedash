package com.drivedash.usermanagement.repository;

import com.drivedash.usermanagement.entity.WithdrawRequest;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long>,
        JpaSpecificationExecutor<WithdrawRequest> {

    Page<WithdrawRequest> findAllByApprovedIsNull(Pageable pageable);

    Page<WithdrawRequest> findAllByApproved(Boolean approved, Pageable pageable);

    Page<WithdrawRequest> findAllByUserId(UUID userId, Pageable pageable);
}
