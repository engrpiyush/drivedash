package com.drivedash.trip.repository;

import com.drivedash.trip.entity.TripRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRequestRepository
        extends JpaRepository<TripRequest, UUID>, JpaSpecificationExecutor<TripRequest> {

    @Query(value = "SELECT * FROM trip_requests WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
           nativeQuery = true)
    List<TripRequest> findAllTrashed();

    @Query(value = "SELECT * FROM trip_requests WHERE id = :id AND deleted_at IS NOT NULL",
           nativeQuery = true)
    Optional<TripRequest> findTrashedById(@Param("id") String id);

    @Modifying
    @Query(value = "UPDATE trip_requests SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM trip_requests WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);

    long countByCurrentStatus(String currentStatus);
}
