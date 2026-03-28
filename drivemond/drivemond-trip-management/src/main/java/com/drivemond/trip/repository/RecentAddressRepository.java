package com.drivemond.trip.repository;

import com.drivemond.trip.entity.RecentAddress;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecentAddressRepository extends JpaRepository<RecentAddress, Long> {
    List<RecentAddress> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}
