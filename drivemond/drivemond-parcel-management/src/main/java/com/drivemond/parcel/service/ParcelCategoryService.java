package com.drivemond.parcel.service;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.core.util.FileStorageService;
import com.drivemond.parcel.dto.ParcelCategoryRequest;
import com.drivemond.parcel.entity.ParcelCategory;
import com.drivemond.parcel.repository.ParcelCategoryRepository;
import jakarta.transaction.Transactional;
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
public class ParcelCategoryService {

    private final ParcelCategoryRepository categoryRepo;
    private final FileStorageService fileStorage;

    public Page<ParcelCategory> getPage(String status, String search, int page, int size) {
        Specification<ParcelCategory> spec = Specification.where(null);

        if ("active".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("active")));
        } else if ("inactive".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isFalse(root.get("active")));
        }
        if (search != null && !search.isBlank()) {
            String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), like));
        }

        return categoryRepo.findAll(spec, PageRequest.of(page, size, Sort.by("name")));
    }

    public List<ParcelCategory> getActiveList() {
        return categoryRepo.findAllByActiveTrueOrderByNameAsc();
    }

    public ParcelCategory findById(UUID id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Parcel category not found"));
    }

    @Transactional
    public void create(ParcelCategoryRequest req) {
        if (categoryRepo.existsByName(req.getName())) {
            throw DrivemondException.conflict("Category name already exists");
        }
        String image = storeImage(req);
        categoryRepo.save(ParcelCategory.builder()
                .name(req.getName())
                .description(req.getDescription())
                .image(image)
                .build());
    }

    @Transactional
    public void update(UUID id, ParcelCategoryRequest req) {
        if (categoryRepo.existsByNameAndIdNot(req.getName(), id)) {
            throw DrivemondException.conflict("Category name already exists");
        }
        ParcelCategory cat = findById(id);
        cat.setName(req.getName());
        cat.setDescription(req.getDescription());
        if (req.getImageFile() != null && !req.getImageFile().isEmpty()) {
            cat.setImage(storeImage(req));
        }
        categoryRepo.save(cat);
    }

    public void toggleStatus(UUID id, boolean active) {
        ParcelCategory cat = findById(id);
        cat.setActive(active);
        categoryRepo.save(cat);
    }

    @Transactional
    public void delete(UUID id) {
        categoryRepo.delete(findById(id));
    }

    public List<ParcelCategory> getTrashed() {
        return categoryRepo.findAllTrashed();
    }

    @Transactional
    public void restore(UUID id) {
        categoryRepo.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        categoryRepo.permanentDelete(id.toString());
    }

    private String storeImage(ParcelCategoryRequest req) {
        if (req.getImageFile() == null || req.getImageFile().isEmpty()) {
            throw DrivemondException.badRequest("Category image is required");
        }
        return fileStorage.store(req.getImageFile(), "parcel/category");
    }
}
