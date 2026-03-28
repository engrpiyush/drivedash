package com.drivemond.trip.repository;

import com.drivemond.trip.entity.TripRequestFee;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRequestFeeRepository extends JpaRepository<TripRequestFee, Long> {
    Optional<TripRequestFee> findByTripRequestId(UUID tripRequestId);
}
