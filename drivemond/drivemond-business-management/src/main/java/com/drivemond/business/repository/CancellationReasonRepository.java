package com.drivemond.business.repository;

import com.drivemond.business.entity.CancellationReason;
import com.drivemond.business.entity.CancellationType;
import com.drivemond.business.entity.CancellationUserType;
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