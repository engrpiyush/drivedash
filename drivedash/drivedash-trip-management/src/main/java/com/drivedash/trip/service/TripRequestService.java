package com.drivedash.trip.service;

import com.drivedash.core.exception.DrivedashException;
import com.drivedash.trip.entity.TripRequest;
import com.drivedash.trip.entity.TripRequestCoordinate;
import com.drivedash.trip.entity.TripRequestFee;
import com.drivedash.trip.entity.TripRequestTime;
import com.drivedash.trip.entity.TripStatus;
import com.drivedash.trip.repository.TripRequestCoordinateRepository;
import com.drivedash.trip.repository.TripRequestFeeRepository;
import com.drivedash.trip.repository.TripRequestRepository;
import com.drivedash.trip.repository.TripRequestTimeRepository;
import com.drivedash.trip.repository.TripStatusRepository;
import jakarta.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripRequestService {

    private final TripRequestRepository tripRepo;
    private final TripStatusRepository statusRepo;
    private final TripRequestFeeRepository feeRepo;
    private final TripRequestTimeRepository timeRepo;
    private final TripRequestCoordinateRepository coordRepo;

    // ── Listing ───────────────────────────────────────────────────────────────

    public Page<TripRequest> getPage(String type, String status, String search, int page, int size) {
        Specification<TripRequest> spec = Specification.where(null);

        if (type != null && !type.isBlank() && !"all".equals(type)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("type"), type));
        }
        if (status != null && !status.isBlank() && !"all".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("currentStatus"), status));
        }
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("refId")), like)
            ));
        }

        return tripRepo.findAll(spec, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    // ── Status counts for dashboard tabs ─────────────────────────────────────

    public Map<String, Long> getStatusCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("pending",    tripRepo.countByCurrentStatus("pending"));
        counts.put("accepted",   tripRepo.countByCurrentStatus("accepted"));
        counts.put("ongoing",    tripRepo.countByCurrentStatus("ongoing"));
        counts.put("completed",  tripRepo.countByCurrentStatus("completed"));
        counts.put("cancelled",  tripRepo.countByCurrentStatus("cancelled"));
        return counts;
    }

    // ── Detail fetch ──────────────────────────────────────────────────────────

    public TripRequest findById(UUID id) {
        return tripRepo.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Trip not found"));
    }

    public Optional<TripStatus> getStatus(UUID tripId) {
        return statusRepo.findByTripRequestId(tripId);
    }

    public Optional<TripRequestFee> getFee(UUID tripId) {
        return feeRepo.findByTripRequestId(tripId);
    }

    public Optional<TripRequestTime> getTime(UUID tripId) {
        return timeRepo.findByTripRequestId(tripId);
    }

    public Optional<TripRequestCoordinate> getCoordinate(UUID tripId) {
        return coordRepo.findByTripRequestId(tripId);
    }

    // ── Trashed / restore / permanent delete ─────────────────────────────────

    public List<TripRequest> getTrashed() {
        return tripRepo.findAllTrashed();
    }

    @Transactional
    public void restore(UUID id) {
        tripRepo.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        tripRepo.permanentDelete(id.toString());
    }

    @Transactional
    public void softDelete(UUID id) {
        tripRepo.delete(findById(id));
    }
}
