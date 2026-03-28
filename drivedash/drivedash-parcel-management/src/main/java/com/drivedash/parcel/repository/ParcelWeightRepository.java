package com.drivedash.parcel.repository;

import com.drivedash.parcel.entity.ParcelWeight;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelWeightRepository
        extends JpaRepository<ParcelWeight, UUID>, JpaSpecificationExecutor<ParcelWeight> {

    List<ParcelWeight> findAllByActiveTrueOrderByMinWeightAsc();

    @Query(value = "SELECT * FROM parcel_weights WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
           nativeQuery = true)
    List<ParcelWeight> findAllTrashed();

    @Modifying
    @Query(value = "UPDATE parcel_weights SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM parcel_weights WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);
}
