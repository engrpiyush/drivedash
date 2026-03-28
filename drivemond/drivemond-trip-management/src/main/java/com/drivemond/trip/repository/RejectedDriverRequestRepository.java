package com.drivemond.trip.repository;

import com.drivemond.trip.entity.RejectedDriverRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectedDriverRequestRepository extends JpaRepository<RejectedDriverRequest, Long> {
    List<RejectedDriverRequest> findAllByTripRequestId(UUID tripRequestId);
    boolean existsByTripRequestIdAndUserId(UUID tripRequestId, UUID userId);
}
