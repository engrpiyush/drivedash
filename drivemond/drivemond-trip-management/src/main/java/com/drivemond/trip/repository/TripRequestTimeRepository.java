package com.drivemond.trip.repository;

import com.drivemond.trip.entity.TripRequestTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRequestTimeRepository extends JpaRepository<TripRequestTime, Long> {
    Optional<TripRequestTime> findByTripRequestId(UUID tripRequestId);
}
