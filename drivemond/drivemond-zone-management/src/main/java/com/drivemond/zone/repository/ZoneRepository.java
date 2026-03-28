package com.drivemond.zone.repository;

import com.drivemond.zone.entity.Zone;
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
public interface ZoneRepository extends JpaRepository<Zone, UUID>, JpaSpecificationExecutor<Zone> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, UUID id);

    // Trashed: bypasses @SQLRestriction with native query
    @Query(value = "SELECT * FROM zones WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
           countQuery = "SELECT COUNT(*) FROM zones WHERE deleted_at IS NOT NULL",
           nativeQuery = true)
    Page<Zone> findAllTrashed(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE zones SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM zones WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);

    @Query(value = "SELECT * FROM zones " +
                   "WHERE ST_Contains(coordinates, ST_GeomFromText(:point, 4326)) = 1 " +
                   "AND deleted_at IS NULL AND is_active = 1",
           nativeQuery = true)
    List<Zone> findByPoint(@Param("point") String pointWkt);
}
