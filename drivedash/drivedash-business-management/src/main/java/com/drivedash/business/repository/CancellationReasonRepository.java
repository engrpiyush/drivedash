package com.drivedash.business.repository;

import com.drivedash.business.entity.CancellationReason;
import com.drivedash.business.entity.CancellationType;
import com.drivedash.business.entity.CancellationUserType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationReasonRepository extends JpaRepository<CancellationReason, UUID> {

    List<CancellationReason> findAllByUserTypeAndCancellationType(
            CancellationUserType userType, CancellationType cancellationType);

    List<CancellationReason> findAllByUserType(CancellationUserType userType);

    List<CancellationReason> findAllByActiveTrue();
}