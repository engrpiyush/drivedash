package com.drivedash.parcel.service;

import com.drivedash.core.annotation.Auditable;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.parcel.dto.ParcelWeightRequest;
import com.drivedash.parcel.entity.ParcelWeight;
import com.drivedash.parcel.repository.ParcelWeightRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParcelWeightService {

    private final ParcelWeightRepository weightRepo;

    public Page<ParcelWeight> getPage(String status, int page, int size) {
        Specification<ParcelWeight> spec = Specification.where(null);

        if ("active".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("active")));
        } else if ("inactive".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isFalse(root.get("active")));
        }

        return weightRepo.findAll(spec, PageRequest.of(page, size, Sort.by("minWeight")));
    }

    public List<ParcelWeight> getActiveList() {
        return weightRepo.findAllByActiveTrueOrderByMinWeightAsc();
    }

    public ParcelWeight findById(UUID id) {
        return weightRepo.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Parcel weight not found"));
    }

    @Auditable(entityClass = ParcelWeight.class, action = "CREATE")
    @Transactional
    public void create(ParcelWeightRequest req) {
        weightRepo.save(ParcelWeight.builder()
                .minWeight(BigDecimal.valueOf(req.getMinWeight()))
                .maxWeight(BigDecimal.valueOf(req.getMaxWeight()))
                .build());
    }

    @Auditable(entityClass = ParcelWeight.class, action = "UPDATE")
    @Transactional
    public void update(UUID id, ParcelWeightRequest req) {
        ParcelWeight w = findById(id);
        w.setMinWeight(BigDecimal.valueOf(req.getMinWeight()));
        w.setMaxWeight(BigDecimal.valueOf(req.getMaxWeight()));
        weightRepo.save(w);
    }

    @Auditable(entityClass = ParcelWeight.class, action = "STATUS_CHANGE")
    public void toggleStatus(UUID id, boolean active) {
        ParcelWeight w = findById(id);
        w.setActive(active);
        weightRepo.save(w);
    }

    @Auditable(entityClass = ParcelWeight.class, action = "DELETE")
    @Transactional
    public void delete(UUID id) {
        weightRepo.delete(findById(id));
    }

    public List<ParcelWeight> getTrashed() {
        return weightRepo.findAllTrashed();
    }

    @Transactional
    public void restore(UUID id) {
        weightRepo.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        weightRepo.permanentDelete(id.toString());
    }
}
