package com.drivedash.zone.controller.api;

import com.drivedash.core.response.ApiResponse;
import com.drivedash.zone.dto.ZoneCoordinatePoint;
import com.drivedash.zone.entity.Zone;
import com.drivedash.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/zones")
@RequiredArgsConstructor
public class ZoneApiController {

    private final ZoneService zoneService;

    /**
     * Returns all active zones with their coordinate polygons.
     * Used by driver and customer apps to determine service area coverage.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActiveZones() {
        List<Zone> zones = zoneService.getZonesForMap("active");
        List<Map<String, Object>> data = new ArrayList<>();
        for (Zone zone : zones) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", zone.getId());
            entry.put("name", zone.getName());
            entry.put("coordinates", zoneService.toCoordinatePoints(zone.getCoordinates()));
            data.add(entry);
        }
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Checks which zone (if any) contains the given lat/lng point.
     * Used by the mobile app to validate driver/customer pickup location.
     */
    @GetMapping("/check-point")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> checkPoint(
            @RequestParam double lat,
            @RequestParam double lng) {
        List<Zone> zones = zoneService.findZonesByPoint(lat, lng);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Zone zone : zones) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", zone.getId());
            entry.put("name", zone.getName());
            data.add(entry);
        }
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
