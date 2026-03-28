package com.drivedash.promotion.repository;

import com.drivedash.promotion.entity.BannerSetup;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerSetupRepository extends JpaRepository<BannerSetup, UUID>,
        JpaSpecificationExecutor<BannerSetup> {

    List<BannerSetup> findAllByIsActiveTrueOrderByCreatedAtDesc();

    @Query(value = "SELECT * FROM banner_setups WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
            nativeQuery = true)
    List<BannerSetup> findAllTrashed();

    @Modifying
    @Query(value = "UPDATE banner_setups SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM banner_setups WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);
}
