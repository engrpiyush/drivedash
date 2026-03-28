package com.drivedash.parcel.repository;

import com.drivedash.parcel.entity.ParcelUserInformation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParcelUserInformationRepository extends JpaRepository<ParcelUserInformation, Long> {
    List<ParcelUserInformation> findAllByTripRequestId(UUID tripRequestId);
}
