package com.drivemond.faremanagement.repository;

import com.drivemond.faremanagement.entity.FareBidding;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FareBiddingRepository extends JpaRepository<FareBidding, UUID> {

    List<FareBidding> findAllByTripRequestIdAndIgnoredFalse(UUID tripRequestId);

    void deleteAllByTripRequestId(UUID tripRequestId);
}
