package com.drivedash.promotion.repository;

import com.drivedash.promotion.entity.CouponSetup;
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
public interface CouponSetupRepository extends JpaRepository<CouponSetup, UUID>,
        JpaSpecificationExecutor<CouponSetup> {

    Optional<CouponSetup> findByCouponCode(String couponCode);

    boolean existsByCouponCode(String couponCode);

    boolean existsByCouponCodeAndIdNot(String couponCode, UUID id);

    @Query(value = "SELECT * FROM coupon_setups WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
            nativeQuery = true)
    List<CouponSetup> findAllTrashed();

    @Modifying
    @Query(value = "UPDATE coupon_setups SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM coupon_setups WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);

    long countByIsActiveTrue();

    long countByIsActiveFalse();
}
