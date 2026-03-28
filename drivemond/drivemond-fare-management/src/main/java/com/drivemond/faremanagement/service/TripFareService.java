package com.drivemond.faremanagement.service;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.faremanagement.dto.CategoryFareRequest;
import com.drivemond.faremanagement.dto.TripFareSetupRequest;
import com.drivemond.faremanagement.entity.TripFare;
import com.drivemond.faremanagement.entity.ZoneWiseDefaultTripFare;
import com.drivemond.faremanagement.repository.TripFareRepository;
import com.drivemond.faremanagement.repository.ZoneWiseDefaultTripFareRepository;
import com.drivemond.vehicle.entity.VehicleCategory;
import com.drivemond.vehicle.repository.VehicleCategoryRepository;
import com.drivemond.zone.entity.Zone;
import com.drivemond.zone.repository.ZoneRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripFareService {

    private final ZoneWiseDefaultTripFareRepository defaultFareRepo;
    private final TripFareRepository tripFareRepo;
    private final ZoneRepository zoneRepo;
    private final VehicleCategoryRepository categoryRepo;

    public List<Zone> getActiveZones() {
        return zoneRepo.findAll().stream()
                .filter(Zone::isActive)
                .toList();
    }

    public Zone findZoneById(UUID zoneId) {
        return zoneRepo.findById(zoneId)
                .orElseThrow(() -> DrivemondException.notFound("Zone not found"));
    }

    public List<VehicleCategory> getActiveCategories() {
        return categoryRepo.findAllByActiveTrueOrderByNameAsc();
    }

    public Optional<ZoneWiseDefaultTripFare> getDefaultFare(UUID zoneId) {
        return defaultFareRepo.findByZoneId(zoneId);
    }

    public List<TripFare> getCategoryFares(UUID zoneId) {
        return tripFareRepo.findAllByZoneId(zoneId);
    }

    public boolean hasSetup(UUID zoneId) {
        return defaultFareRepo.existsByZoneId(zoneId);
    }

    @Transactional
    public void setup(UUID zoneId, TripFareSetupRequest req) {
        // 1. Upsert zone-wide default
        ZoneWiseDefaultTripFare defaultFare = defaultFareRepo.findByZoneId(zoneId)
                .orElse(ZoneWiseDefaultTripFare.builder().zoneId(zoneId).build());

        defaultFare.setBaseFare(req.getBaseFare());
        defaultFare.setBaseFarePerKm(req.getBaseFarePerKm());
        defaultFare.setWaitingFeePerMin(req.getWaitingFeePerMin());
        defaultFare.setCancellationFeePercent(req.getCancellationFeePercent());
        defaultFare.setMinCancellationFee(req.getMinCancellationFee());
        defaultFare.setIdleFeePerMin(req.getIdleFeePerMin());
        defaultFare.setTripDelayFeePerMin(req.getTripDelayFeePerMin());
        defaultFare.setPenaltyFeeForCancel(req.getPenaltyFeeForCancel());
        defaultFare.setFeeAddToNext(req.getFeeAddToNext());
        defaultFare.setCategoryWiseFare(req.isCategoryWiseFare());
        defaultFare = defaultFareRepo.save(defaultFare);

        // 2. Per-category fares
        if (req.isCategoryWiseFare() && req.getCategoryFares() != null) {
            for (CategoryFareRequest cat : req.getCategoryFares()) {
                TripFare fare = tripFareRepo
                        .findByZoneIdAndVehicleCategoryId(zoneId, cat.getCategoryId())
                        .orElse(TripFare.builder()
                                .zoneId(zoneId)
                                .vehicleCategoryId(cat.getCategoryId())
                                .build());
                fare.setZoneWiseDefaultTripFareId(defaultFare.getId());
                fare.setBaseFare(BigDecimal.valueOf(cat.getBaseFare()));
                fare.setBaseFarePerKm(BigDecimal.valueOf(cat.getBaseFarePerKm()));
                fare.setWaitingFeePerMin(BigDecimal.valueOf(cat.getWaitingFeePerMin()));
                fare.setCancellationFeePercent(BigDecimal.valueOf(cat.getCancellationFeePercent()));
                fare.setMinCancellationFee(BigDecimal.valueOf(cat.getMinCancellationFee()));
                fare.setIdleFeePerMin(BigDecimal.valueOf(cat.getIdleFeePerMin()));
                fare.setTripDelayFeePerMin(BigDecimal.valueOf(cat.getTripDelayFeePerMin()));
                fare.setPenaltyFeeForCancel(BigDecimal.valueOf(cat.getPenaltyFeeForCancel()));
                fare.setFeeAddToNext(BigDecimal.valueOf(cat.getFeeAddToNext()));
                tripFareRepo.save(fare);
            }
        } else {
            // Not using category-wise fares — remove any lingering per-category rows
            tripFareRepo.deleteAllByZoneId(zoneId);
        }
    }

    /**
     * Returns TripFare for a specific zone+category, falling back to the zone default.
     */
    public TripFare getEffectiveFare(UUID zoneId, UUID categoryId) {
        return tripFareRepo.findByZoneIdAndVehicleCategoryId(zoneId, categoryId)
                .orElseGet(() -> {
                    ZoneWiseDefaultTripFare def = defaultFareRepo.findByZoneId(zoneId)
                            .orElseThrow(() -> DrivemondException.notFound("No fare configured for this zone"));
                    return TripFare.builder()
                            .zoneId(zoneId)
                            .vehicleCategoryId(categoryId)
                            .zoneWiseDefaultTripFareId(def.getId())
                            .baseFare(BigDecimal.valueOf(def.getBaseFare()))
                            .baseFarePerKm(BigDecimal.valueOf(def.getBaseFarePerKm()))
                            .waitingFeePerMin(BigDecimal.valueOf(def.getWaitingFeePerMin()))
                            .cancellationFeePercent(BigDecimal.valueOf(def.getCancellationFeePercent()))
                            .minCancellationFee(BigDecimal.valueOf(def.getMinCancellationFee()))
                            .idleFeePerMin(BigDecimal.valueOf(def.getIdleFeePerMin()))
                            .tripDelayFeePerMin(BigDecimal.valueOf(def.getTripDelayFeePerMin()))
                            .penaltyFeeForCancel(BigDecimal.valueOf(def.getPenaltyFeeForCancel()))
                            .feeAddToNext(BigDecimal.valueOf(def.getFeeAddToNext()))
                            .build();
                });
    }
}
