package com.drivedash.usermanagement.repository;

import com.drivedash.usermanagement.entity.LevelAccess;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelAccessRepository extends JpaRepository<LevelAccess, Long> {

    Optional<LevelAccess> findByLevelIdAndUserType(UUID levelId, String userType);

    void deleteByLevelId(UUID levelId);
}
