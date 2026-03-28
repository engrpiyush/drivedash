package com.drivemond.usermanagement.repository;

import com.drivemond.usermanagement.entity.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
