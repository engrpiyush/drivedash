package com.drivedash.usermanagement.repository;

import com.drivedash.usermanagement.entity.DriverDetail;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverDetailRepository extends JpaRepository<DriverDetail, Long> {

    Optional<DriverDetail> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
