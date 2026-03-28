package com.drivemond.faremanagement.repository;

import com.drivemond.faremanagement.entity.TripFare;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripFareRepository extends JpaRepository<TripFare, UUID> {

    List<TripFare> findAllByZoneId(UUID zoneId);

    Optional<TripFare> findByZoneIdAndVehicleCategoryId(UUID zoneId, UUID vehicleCategoryId);

    boolean existsByZoneId(UUID zoneId);

    void deleteAllByZoneId(UUID zoneId);
}
