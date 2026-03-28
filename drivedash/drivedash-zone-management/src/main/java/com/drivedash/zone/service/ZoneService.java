package com.drivedash.zone.service;

import com.drivedash.core.exception.DrivedashException;
import com.drivedash.zone.dto.ZoneCoordinatePoint;
import com.drivedash.zone.dto.ZoneRequest;
import com.drivedash.zone.entity.Zone;
import com.drivedash.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    private final ZoneRepository zoneRepository;

    public Zone create(ZoneRequest request) {
        if (zoneRepository.existsByName(request.getName())) {
            throw DrivedashException.conflict("Zone name already exists");
        }
        if (!StringUtils.hasText(request.getCoordinates())) {
            throw DrivedashException.badRequest("Please draw the zone on the map");
        }
        Zone zone = Zone.builder()
                .name(request.getName())
                .coordinates(parseCoordinates(request.getCoordinates()))
                .active(true)
                .build();
        return zoneRepository.save(zone);
    }

    public Zone update(UUID id, ZoneRequest request) {
        Zone zone = findById(id);
        if (zoneRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw DrivedashException.conflict("Zone name already exists");
        }
        zone.setName(request.getName());
        if (StringUtils.hasText(request.getCoordinates())) {
            zone.setCoordinates(parseCoordinates(request.getCoordinates()));
        }
        return zoneRepository.save(zone);
    }

    @Transactional(readOnly = true)
    public Zone findById(UUID id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Zone not found"));
    }

    @Transactional(readOnly = true)
    public Page<Zone> getZonesPage(String search, String status, Pageable pageable) {
        Specification<Zone> spec = buildSpec(search, status);
        return zoneRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public List<Zone> getZonesForMap(String status) {
        Specification<Zone> spec = buildSpec(null, status);
        return zoneRepository.findAll(spec);
    }

    @Transactional
    public void delete(UUID id) {
        Zone zone = findById(id);
        zoneRepository.delete(zone); // triggers @SQLDelete → sets deleted_at
    }

    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        Zone zone = findById(id);
        zone.setActive(active);
        zoneRepository.save(zone);
    }

    @Transactional(readOnly = true)
    public Page<Zone> getTrashedZones(Pageable pageable) {
        return zoneRepository.findAllTrashed(pageable);
    }

    @Transactional
    public void restore(UUID id) {
        zoneRepository.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        zoneRepository.permanentDelete(id.toString());
    }

    @Transactional(readOnly = true)
    public List<Zone> findZonesByPoint(double lat, double lng) {
        String pointWkt = String.format(Locale.US, "POINT(%f %f)", lng, lat);
        return zoneRepository.findByPoint(pointWkt);
    }

    // ── Coordinate helpers ───────────────────────────────────────────────────

    /**
     * Parses Google Maps getPath().getArray().toString() output.
     * Format: "(lat, lng),(lat, lng),..." or "(lat lng),(lat lng),..."
     * JTS convention: Coordinate(x=lng, y=lat)
     */
    public Polygon parseCoordinates(String coordinatesStr) {
        List<Coordinate> coords = new ArrayList<>();
        // Split on ),(  to get individual (lat, lng) tokens
        String[] tokens = coordinatesStr.split("\\),\\s*\\(");
        for (String token : tokens) {
            String cleaned = token.replaceAll("[()\\s]", "");
            String[] parts = cleaned.split(",");
            if (parts.length >= 2) {
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                coords.add(new Coordinate(lng, lat));
            }
        }
        if (coords.size() < 3) {
            throw DrivedashException.badRequest("A zone requires at least 3 coordinate points");
        }
        // Close the polygon ring
        if (!coords.get(0).equals2D(coords.get(coords.size() - 1))) {
            coords.add(new Coordinate(coords.get(0)));
        }
        return GF.createPolygon(coords.toArray(new Coordinate[0]));
    }

    /**
     * Converts a JTS Polygon to a list of {lat, lng} DTOs for Thymeleaf / JSON.
     * Skips the closing duplicate coordinate.
     */
    public List<ZoneCoordinatePoint> toCoordinatePoints(Polygon polygon) {
        if (polygon == null) return Collections.emptyList();
        Coordinate[] raw = polygon.getCoordinates();
        List<ZoneCoordinatePoint> result = new ArrayList<>(raw.length - 1);
        // raw[last] == raw[0] (closed ring) — skip last
        for (int i = 0; i < raw.length - 1; i++) {
            result.add(new ZoneCoordinatePoint(raw[i].y, raw[i].x));
        }
        return result;
    }

    /**
     * Returns centroid of polygon as {lat, lng} or null.
     */
    public ZoneCoordinatePoint getCentroid(Polygon polygon) {
        if (polygon == null) return new ZoneCoordinatePoint(23.757989, 90.360587); // default center
        Point centroid = polygon.getCentroid();
        return new ZoneCoordinatePoint(centroid.getY(), centroid.getX());
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private Specification<Zone> buildSpec(String search, String status) {
        Specification<Zone> spec = Specification.where(null);
        if (StringUtils.hasText(search)) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
        }
        Boolean active = parseStatus(status);
        if (active != null) {
            boolean activeVal = active;
            spec = spec.and((root, q, cb) -> cb.equal(root.get("active"), activeVal));
        }
        return spec;
    }

    private Boolean parseStatus(String status) {
        if ("active".equals(status)) return Boolean.TRUE;
        if ("inactive".equals(status)) return Boolean.FALSE;
        return null;
    }
}
