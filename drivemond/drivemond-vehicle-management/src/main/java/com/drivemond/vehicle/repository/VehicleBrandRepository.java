package com.drivemond.vehicle.repository;

import com.drivemond.vehicle.entity.VehicleBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleBrandRepository extends JpaRepository<VehicleBrand, UUID>,
        JpaSpecificationExecutor<VehicleBrand> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, UUID id);

    List<VehicleBrand> findAllByActiveTrue();

    @Query(value = "SELECT * FROM vehicle_brands WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
           countQuery = "SELECT COUNT(*) FROM vehicle_brands WHERE deleted_at IS NOT NULL",
           nativeQuery = true)
    Page<VehicleBrand> findAllTrashed(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE vehicle_brands SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM vehicle_brands WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);
}
