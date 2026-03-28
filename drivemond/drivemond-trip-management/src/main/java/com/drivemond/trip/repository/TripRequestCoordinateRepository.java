package com.drivemond.trip.repository;

import com.drivemond.trip.entity.TripRequestCoordinate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRequestCoordinateRepository extends JpaRepository<TripRequestCoordinate, Long> {
    Optional<TripRequestCoordinate> findByTripRequestId(UUID tripRequestId);
}
