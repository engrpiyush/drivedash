package com.drivemond.usermanagement.repository;

import com.drivemond.usermanagement.entity.LevelAccess;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelAccessRepository extends JpaRepository<LevelAccess, Long> {

    Optional<LevelAccess> findByLevelIdAndUserType(UUID levelId, String userType);

    void deleteByLevelId(UUID levelId);
}
