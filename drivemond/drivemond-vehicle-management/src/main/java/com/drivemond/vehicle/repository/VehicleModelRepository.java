package com.drivemond.vehicle.repository;

import com.drivemond.vehicle.entity.VehicleModel;
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
public interface VehicleModelRepository extends JpaRepository<VehicleModel, UUID>,
        JpaSpecificationExecutor<VehicleModel> {

    boolean existsByNameAndBrandId(String name, UUID brandId);
    boolean existsByNameAndBrandIdAndIdNot(String name, UUID brandId, UUID id);

    List<VehicleModel> findAllByBrandIdAndActiveTrue(UUID brandId);
    List<VehicleModel> findAllByActiveTrue();

    @Query(value = "SELECT * FROM vehicle_models WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
           countQuery = "SELECT COUNT(*) FROM vehicle_models WHERE deleted_at IS NOT NULL",
           nativeQuery = true)
    Page<VehicleModel> findAllTrashed(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE vehicle_models SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM vehicle_models WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);
}
