package com.drivedash.faremanagement.repository;

import com.drivedash.faremanagement.entity.ZoneWiseDefaultTripFare;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneWiseDefaultTripFareRepository extends JpaRepository<ZoneWiseDefaultTripFare, UUID> {

    Optional<ZoneWiseDefaultTripFare> findByZoneId(UUID zoneId);

    boolean existsByZoneId(UUID zoneId);
}
