package com.drivemond.vehicle.service;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.core.util.FileStorageService;
import com.drivemond.vehicle.dto.VehicleBrandRequest;
import com.drivemond.vehicle.entity.VehicleBrand;
import com.drivemond.vehicle.repository.VehicleBrandRepository;
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
public class VehicleBrandService {

    private final VehicleBrandRepository brandRepository;
    private final FileStorageService fileStorageService;

    public VehicleBrand create(VehicleBrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw DrivemondException.conflict("Brand name already exists");
        }
        String imagePath = storeImage(request.getImage());
        VehicleBrand brand = VehicleBrand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .image(imagePath)
                .active(true)
                .build();
        return brandRepository.save(brand);
    }

    public VehicleBrand update(UUID id, VehicleBrandRequest request) {
        VehicleBrand brand = findById(id);
        if (brandRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw DrivemondException.conflict("Brand name already exists");
        }
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            fileStorageService.delete(brand.getImage());
            brand.setImage(storeImage(request.getImage()));
        }
        return brandRepository.save(brand);
    }

    @Transactional(readOnly = true)
    public VehicleBrand findById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Brand not found"));
    }

    @Transactional(readOnly = true)
    public Page<VehicleBrand> getPage(String search, String status, Pageable pageable) {
        return brandRepository.findAll(buildSpec(search, status), pageable);
    }

    @Transactional(readOnly = true)
    public List<VehicleBrand> getActiveList() {
        return brandRepository.findAllByActiveTrue();
    }

    @Transactional
    public void delete(UUID id) {
        brandRepository.delete(findById(id));
    }

    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        VehicleBrand brand = findById(id);
        brand.setActive(active);
        brandRepository.save(brand);
    }

    @Transactional(readOnly = true)
    public Page<VehicleBrand> getTrashed(Pageable pageable) {
        return brandRepository.findAllTrashed(pageable);
    }

    @Transactional
    public void restore(UUID id) {
        brandRepository.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        brandRepository.permanentDelete(id.toString());
    }

    private String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        return fileStorageService.store(file, "vehicle/brand");
    }

    private Specification<VehicleBrand> buildSpec(String search, String status) {
        Specification<VehicleBrand> spec = Specification.where(null);
        if (StringUtils.hasText(search)) {
            spec = spec.and((r, q, cb) ->
                    cb.like(cb.lower(r.get("name")), "%" + search.toLowerCase() + "%"));
        }
        if ("active".equals(status)) spec = spec.and((r, q, cb) -> cb.isTrue(r.get("active")));
        if ("inactive".equals(status)) spec = spec.and((r, q, cb) -> cb.isFalse(r.get("active")));
        return spec;
    }
}
