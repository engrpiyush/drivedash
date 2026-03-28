package com.drivemond.vehicle.service;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.core.util.FileStorageService;
import com.drivemond.vehicle.dto.VehicleCategoryRequest;
import com.drivemond.vehicle.entity.VehicleCategory;
import com.drivemond.vehicle.repository.VehicleCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleCategoryService {

    private final VehicleCategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    public VehicleCategory create(VehicleCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw DrivemondException.conflict("Category name already exists");
        }
        String imagePath = storeImage(request.getImage());
        VehicleCategory cat = VehicleCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .image(imagePath)
                .active(true)
                .build();
        return categoryRepository.save(cat);
    }

    public VehicleCategory update(UUID id, VehicleCategoryRequest request) {
        VehicleCategory cat = findById(id);
        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw DrivemondException.conflict("Category name already exists");
        }
        cat.setName(request.getName());
        cat.setDescription(request.getDescription());
        cat.setType(request.getType());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            fileStorageService.delete(cat.getImage());
            cat.setImage(storeImage(request.getImage()));
        }
        return categoryRepository.save(cat);
    }

    @Transactional(readOnly = true)
    public VehicleCategory findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Category not found"));
    }

    @Transactional(readOnly = true)
    public Page<VehicleCategory> getPage(String search, String status, Pageable pageable) {
        return categoryRepository.findAll(buildSpec(search, status), pageable);
    }

    @Transactional(readOnly = true)
    public List<VehicleCategory> getActiveList() {
        return categoryRepository.findAllByActiveTrue();
    }

    @Transactional
    public void delete(UUID id) {
        categoryRepository.delete(findById(id));
    }

    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        VehicleCategory cat = findById(id);
        cat.setActive(active);
        categoryRepository.save(cat);
    }

    @Transactional(readOnly = true)
    public Page<VehicleCategory> getTrashed(Pageable pageable) {
        return categoryRepository.findAllTrashed(pageable);
    }

    @Transactional
    public void restore(UUID id) {
        categoryRepository.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        categoryRepository.permanentDelete(id.toString());
    }

    private String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        return fileStorageService.store(file, "vehicle/category");
    }

    private Specification<VehicleCategory> buildSpec(String search, String status) {
        Specification<VehicleCategory> spec = Specification.where(null);
        if (StringUtils.hasText(search)) {
            spec = spec.and((r, q, cb) ->
                    cb.like(cb.lower(r.get("name")), "%" + search.toLowerCase() + "%"));
        }
        if ("active".equals(status)) spec = spec.and((r, q, cb) -> cb.isTrue(r.get("active")));
        if ("inactive".equals(status)) spec = spec.and((r, q, cb) -> cb.isFalse(r.get("active")));
        return spec;
    }
}
