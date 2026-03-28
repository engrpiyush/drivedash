package com.drivemond.trip.repository;

import com.drivemond.trip.entity.TripStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripStatusRepository extends JpaRepository<TripStatus, Long> {
    Optional<TripStatus> findByTripRequestId(UUID tripRequestId);
}
