package com.drivemond.faremanagement.controller.api;

import com.drivemond.faremanagement.entity.ParcelFare;
import com.drivemond.faremanagement.entity.TripFare;
import com.drivemond.faremanagement.service.ParcelFareService;
import com.drivemond.faremanagement.service.TripFareService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fare")
@RequiredArgsConstructor
public class FareApiController {

    private final TripFareService tripFareService;
    private final ParcelFareService parcelFareService;

    /**
     * Returns the effective trip fare for a zone+category combination.
     * Falls back to zone-wide defaults if no per-category fare is configured.
     */
    @GetMapping("/trip")
    public ResponseEntity<TripFare> getTripFare(@RequestParam UUID zoneId,
                                                @RequestParam UUID categoryId) {
        return ResponseEntity.ok(tripFareService.getEffectiveFare(zoneId, categoryId));
    }

    /**
     * Returns the parcel fare for a given zone.
     */
    @GetMapping("/parcel")
    public ResponseEntity<ParcelFare> getParcelFare(@RequestParam UUID zoneId) {
        return parcelFareService.getFareByZone(zoneId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
