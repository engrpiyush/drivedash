package com.drivedash.usermanagement.repository;

import com.drivedash.usermanagement.entity.UserLevel;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, UUID>,
        JpaSpecificationExecutor<UserLevel> {

    boolean existsByNameAndUserType(String name, String userType);

    boolean existsByNameAndUserTypeAndIdNot(String name, String userType, UUID id);

    List<UserLevel> findAllByUserTypeAndActiveTrueOrderBySequenceAsc(String userType);

    @Query(value = "SELECT * FROM user_levels WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC",
           countQuery = "SELECT COUNT(*) FROM user_levels WHERE deleted_at IS NOT NULL",
           nativeQuery = true)
    Page<UserLevel> findAllTrashed(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE user_levels SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    void restore(@Param("id") String id);

    @Modifying
    @Query(value = "DELETE FROM user_levels WHERE id = :id", nativeQuery = true)
    void permanentDelete(@Param("id") String id);
}
