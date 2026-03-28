package com.drivemond.parcel.repository;

import com.drivemond.parcel.entity.ParcelInformation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelInformationRepository extends JpaRepository<ParcelInformation, Long> {
    Optional<ParcelInformation> findByTripRequestId(UUID tripRequestId);
}
