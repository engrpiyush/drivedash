package com.drivedash.faremanagement.repository;

import com.drivedash.faremanagement.entity.ParcelFare;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelFareRepository extends JpaRepository<ParcelFare, UUID> {

    Optional<ParcelFare> findByZoneId(UUID zoneId);

    @Query(value = "SELECT * FROM parcel_fares WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<ParcelFare> findAllTrashed();

    @Modifying
    @Query(value = "UPDATE parcel_fares SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM parcel_fares WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);
}
