package com.drivemond.vehicle.repository;

import com.drivemond.vehicle.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>,
        JpaSpecificationExecutor<Vehicle> {

    boolean existsByDriverId(UUID driverId);
    boolean existsByDriverIdAndIdNot(UUID driverId, UUID id);

    Optional<Vehicle> findByDriverId(UUID driverId);

    @Query(value = "SELECT * FROM vehicles WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
           countQuery = "SELECT COUNT(*) FROM vehicles WHERE deleted_at IS NOT NULL",
           nativeQuery = true)
    Page<Vehicle> findAllTrashed(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE vehicles SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM vehicles WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);
}
