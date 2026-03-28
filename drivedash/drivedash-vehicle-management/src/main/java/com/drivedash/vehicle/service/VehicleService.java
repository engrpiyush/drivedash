package com.drivedash.vehicle.service;

import com.drivedash.auth.repository.UserRepository;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.core.util.FileStorageService;
import com.drivedash.vehicle.dto.VehicleRequest;
import com.drivedash.vehicle.entity.Vehicle;
import com.drivedash.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public Vehicle create(VehicleRequest request) {
        if (vehicleRepository.existsByDriverId(request.getDriverId())) {
            throw DrivedashException.conflict("Driver already has a vehicle assigned");
        }
        List<String> docPaths = storeDocuments(request.getDocuments());
        long count = vehicleRepository.count();
        Vehicle vehicle = Vehicle.builder()
                .refId(String.valueOf(count + 100000))
                .brandId(request.getBrandId())
                .modelId(request.getModelId())
                .categoryId(request.getCategoryId())
                .licencePlateNumber(request.getLicencePlateNumber())
                .licenceExpireDate(request.getLicenceExpireDate())
                .vinNumber(request.getVinNumber())
                .transmission(request.getTransmission())
                .fuelType(request.getFuelType())
                .ownership(request.getOwnership())
                .driverId(request.getDriverId())
                .documents(docPaths)
                .active(false)
                .build();
        return vehicleRepository.save(vehicle);
    }

    public Vehicle update(UUID id, VehicleRequest request) {
        Vehicle vehicle = findById(id);
        if (vehicleRepository.existsByDriverIdAndIdNot(request.getDriverId(), id)) {
            throw DrivedashException.conflict("Driver already has a vehicle assigned");
        }
        vehicle.setBrandId(request.getBrandId());
        vehicle.setModelId(request.getModelId());
        vehicle.setCategoryId(request.getCategoryId());
        vehicle.setLicencePlateNumber(request.getLicencePlateNumber());
        vehicle.setLicenceExpireDate(request.getLicenceExpireDate());
        vehicle.setVinNumber(request.getVinNumber());
        vehicle.setTransmission(request.getTransmission());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setOwnership(request.getOwnership());
        vehicle.setDriverId(request.getDriverId());
        if (request.getDocuments() != null && !request.getDocuments().isEmpty()) {
            List<String> newDocs = storeDocuments(request.getDocuments());
            List<String> merged = new ArrayList<>(vehicle.getDocuments() != null ? vehicle.getDocuments() : List.of());
            merged.addAll(newDocs);
            vehicle.setDocuments(merged);
        }
        return vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public Vehicle findById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Vehicle not found"));
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> getPage(String search, String status, Pageable pageable) {
        return vehicleRepository.findAll(buildSpec(search, status), pageable);
    }

    @Transactional
    public void delete(UUID id) {
        vehicleRepository.delete(findById(id));
    }

    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        Vehicle vehicle = findById(id);
        vehicle.setActive(active);
        vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> getTrashed(Pageable pageable) {
        return vehicleRepository.findAllTrashed(pageable);
    }

    @Transactional
    public void restore(UUID id) {
        vehicleRepository.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        vehicleRepository.permanentDelete(id.toString());
    }

    private List<String> storeDocuments(List<MultipartFile> files) {
        List<String> paths = new ArrayList<>();
        if (files == null) return paths;
        for (MultipartFile f : files) {
            if (f != null && !f.isEmpty()) {
                paths.add(fileStorageService.store(f, "vehicle/documents"));
            }
        }
        return paths;
    }

    private Specification<Vehicle> buildSpec(String search, String status) {
        Specification<Vehicle> spec = Specification.where(null);
        if (StringUtils.hasText(search)) {
            spec = spec.and((r, q, cb) -> cb.or(
                    cb.like(cb.lower(r.get("licencePlateNumber")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(r.get("vinNumber")), "%" + search.toLowerCase() + "%")
            ));
        }
        if ("active".equals(status)) spec = spec.and((r, q, cb) -> cb.isTrue(r.get("active")));
        if ("inactive".equals(status)) spec = spec.and((r, q, cb) -> cb.isFalse(r.get("active")));
        return spec;
    }
}
