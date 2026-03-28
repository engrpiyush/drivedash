package com.drivedash.faremanagement.service;

import com.drivedash.core.annotation.Auditable;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.faremanagement.dto.ParcelFareSetupRequest;
import com.drivedash.faremanagement.entity.ParcelFare;
import com.drivedash.faremanagement.repository.ParcelFareRepository;
import com.drivedash.zone.entity.Zone;
import com.drivedash.zone.repository.ZoneRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParcelFareService {

    private final ParcelFareRepository parcelFareRepo;
    private final ZoneRepository zoneRepo;

    public List<Zone> getActiveZones() {
        return zoneRepo.findAll().stream()
                .filter(Zone::isActive)
                .toList();
    }

    public Zone findZoneById(UUID zoneId) {
        return zoneRepo.findById(zoneId)
                .orElseThrow(() -> DrivedashException.notFound("Zone not found"));
    }

    public Optional<ParcelFare> getFareByZone(UUID zoneId) {
        return parcelFareRepo.findByZoneId(zoneId);
    }

    public boolean hasSetup(UUID zoneId) {
        return parcelFareRepo.findByZoneId(zoneId).isPresent();
    }

    @Auditable(entityClass = ParcelFare.class, action = "UPDATE")
    @Transactional
    public void setup(UUID zoneId, ParcelFareSetupRequest req) {
        ParcelFare fare = parcelFareRepo.findByZoneId(zoneId)
                .orElse(ParcelFare.builder().zoneId(zoneId).build());

        fare.setBaseFare(BigDecimal.valueOf(req.getBaseFare()));
        fare.setBaseFarePerKm(BigDecimal.valueOf(req.getBaseFarePerKm()));
        fare.setCancellationFeePercent(BigDecimal.valueOf(req.getCancellationFeePercent()));
        fare.setMinCancellationFee(BigDecimal.valueOf(req.getMinCancellationFee()));
        parcelFareRepo.save(fare);
    }

    public List<ParcelFare> getAllActive() {
        return parcelFareRepo.findAll();
    }

    public List<ParcelFare> getTrashed() {
        return parcelFareRepo.findAllTrashed();
    }

    @Transactional
    public void restore(UUID id) {
        parcelFareRepo.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        parcelFareRepo.permanentDelete(id.toString());
    }

    public ParcelFare findById(UUID id) {
        return parcelFareRepo.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Parcel fare not found"));
    }

    @Auditable(entityClass = ParcelFare.class, action = "DELETE")
    @Transactional
    public void delete(UUID id) {
        parcelFareRepo.delete(findById(id));
    }
}
