package com.drivedash.vehicle.service;

import com.drivedash.core.annotation.Auditable;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.core.util.FileStorageService;
import com.drivedash.vehicle.dto.VehicleModelRequest;
import com.drivedash.vehicle.entity.VehicleModel;
import com.drivedash.vehicle.repository.VehicleModelRepository;
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
public class VehicleModelService {

    private final VehicleModelRepository modelRepository;
    private final FileStorageService fileStorageService;

    @Auditable(entityClass = VehicleModel.class, action = "CREATE")
    public VehicleModel create(VehicleModelRequest request) {
        if (modelRepository.existsByNameAndBrandId(request.getName(), request.getBrandId())) {
            throw DrivedashException.conflict("Model name already exists for this brand");
        }
        String imagePath = storeImage(request.getImage());
        VehicleModel model = VehicleModel.builder()
                .name(request.getName())
                .brandId(request.getBrandId())
                .description(request.getDescription())
                .seatCapacity(request.getSeatCapacity())
                .maximumWeight(request.getMaximumWeight())
                .hatchBagCapacity(request.getHatchBagCapacity())
                .engine(request.getEngine())
                .image(imagePath)
                .active(true)
                .build();
        return modelRepository.save(model);
    }

    @Auditable(entityClass = VehicleModel.class, action = "UPDATE")
    public VehicleModel update(UUID id, VehicleModelRequest request) {
        VehicleModel model = findById(id);
        if (modelRepository.existsByNameAndBrandIdAndIdNot(request.getName(), request.getBrandId(), id)) {
            throw DrivedashException.conflict("Model name already exists for this brand");
        }
        model.setName(request.getName());
        model.setBrandId(request.getBrandId());
        model.setDescription(request.getDescription());
        model.setSeatCapacity(request.getSeatCapacity());
        model.setMaximumWeight(request.getMaximumWeight());
        model.setHatchBagCapacity(request.getHatchBagCapacity());
        model.setEngine(request.getEngine());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            fileStorageService.delete(model.getImage());
            model.setImage(storeImage(request.getImage()));
        }
        return modelRepository.save(model);
    }

    @Transactional(readOnly = true)
    public VehicleModel findById(UUID id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Model not found"));
    }

    @Transactional(readOnly = true)
    public Page<VehicleModel> getPage(String search, UUID brandId, String status, Pageable pageable) {
        return modelRepository.findAll(buildSpec(search, brandId, status), pageable);
    }

    @Transactional(readOnly = true)
    public List<VehicleModel> getActiveByBrand(UUID brandId) {
        return modelRepository.findAllByBrandIdAndActiveTrue(brandId);
    }

    @Transactional(readOnly = true)
    public List<VehicleModel> getAllActive() {
        return modelRepository.findAllByActiveTrue();
    }

    @Auditable(entityClass = VehicleModel.class, action = "DELETE")
    @Transactional
    public void delete(UUID id) {
        modelRepository.delete(findById(id));
    }

    @Auditable(entityClass = VehicleModel.class, action = "STATUS_CHANGE")
    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        VehicleModel model = findById(id);
        model.setActive(active);
        modelRepository.save(model);
    }

    @Transactional(readOnly = true)
    public Page<VehicleModel> getTrashed(Pageable pageable) {
        return modelRepository.findAllTrashed(pageable);
    }

    @Transactional
    public void restore(UUID id) {
        modelRepository.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        modelRepository.permanentDelete(id.toString());
    }

    private String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        return fileStorageService.store(file, "vehicle/model");
    }

    private Specification<VehicleModel> buildSpec(String search, UUID brandId, String status) {
        Specification<VehicleModel> spec = Specification.where(null);
        if (StringUtils.hasText(search)) {
            spec = spec.and((r, q, cb) ->
                    cb.like(cb.lower(r.get("name")), "%" + search.toLowerCase() + "%"));
        }
        if (brandId != null) {
            spec = spec.and((r, q, cb) -> cb.equal(r.get("brandId"), brandId));
        }
        if ("active".equals(status)) spec = spec.and((r, q, cb) -> cb.isTrue(r.get("active")));
        if ("inactive".equals(status)) spec = spec.and((r, q, cb) -> cb.isFalse(r.get("active")));
        return spec;
    }
}
