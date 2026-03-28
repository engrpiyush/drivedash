package com.drivedash.auth.repository;

import com.drivedash.auth.entity.User;
import com.drivedash.auth.entity.UserType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for {@link User}.
 *
 * <p>{@link JpaSpecificationExecutor} is included to support dynamic filtering
 * (e.g., user search with multiple optional criteria) without writing
 * boilerplate query methods – the Spring Data equivalent of Eloquent scopes.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    long countByUserType(UserType userType);
}
