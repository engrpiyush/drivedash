package com.drivedash.promotion.service;

import com.drivedash.core.annotation.Auditable;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.core.util.FileStorageService;
import com.drivedash.promotion.dto.BannerSetupRequest;
import com.drivedash.promotion.entity.BannerSetup;
import com.drivedash.promotion.repository.BannerSetupRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BannerSetupService {

    private final BannerSetupRepository bannerRepo;
    private final FileStorageService fileStorageService;

    public Page<BannerSetup> getPage(String search, String status, int page, int size) {
        Specification<BannerSetup> spec = Specification.where(null);

        if ("active".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("isActive")));
        } else if ("inactive".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isFalse(root.get("isActive")));
        }
        if (StringUtils.hasText(search)) {
            final String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), like));
        }

        return bannerRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public List<BannerSetup> getActiveList() {
        return bannerRepo.findAllByIsActiveTrueOrderByCreatedAtDesc();
    }

    public BannerSetup findById(UUID id) {
        return bannerRepo.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Banner not found"));
    }

    @Auditable(entityClass = BannerSetup.class, action = "CREATE")
    @Transactional
    public void create(BannerSetupRequest req) {
        String image = null;
        if (req.getImageFile() != null && !req.getImageFile().isEmpty()) {
            image = fileStorageService.store(req.getImageFile(), "banners");
        }

        bannerRepo.save(BannerSetup.builder()
                .name(req.getName())
                .description(req.getDescription())
                .timePeriod(req.isAllTime() ? "all_time" : req.getTimePeriod())
                .displayPosition(req.getDisplayPosition())
                .redirectLink(req.getRedirectLink())
                .bannerGroup(req.getBannerGroup())
                .startDate(req.isAllTime() ? null : req.getStartDate())
                .endDate(req.isAllTime() ? null : req.getEndDate())
                .image(image)
                .build());
    }

    @Auditable(entityClass = BannerSetup.class, action = "UPDATE")
    @Transactional
    public void update(UUID id, BannerSetupRequest req) {
        BannerSetup banner = findById(id);
        banner.setName(req.getName());
        banner.setDescription(req.getDescription());
        banner.setTimePeriod(req.isAllTime() ? "all_time" : req.getTimePeriod());
        banner.setDisplayPosition(req.getDisplayPosition());
        banner.setRedirectLink(req.getRedirectLink());
        banner.setBannerGroup(req.getBannerGroup());
        banner.setStartDate(req.isAllTime() ? null : req.getStartDate());
        banner.setEndDate(req.isAllTime() ? null : req.getEndDate());

        if (req.getImageFile() != null && !req.getImageFile().isEmpty()) {
            banner.setImage(fileStorageService.store(req.getImageFile(), "banners"));
        }
        bannerRepo.save(banner);
    }

    @Auditable(entityClass = BannerSetup.class, action = "STATUS_CHANGE")
    public void toggleStatus(UUID id, boolean active) {
        BannerSetup banner = findById(id);
        banner.setActive(active);
        bannerRepo.save(banner);
    }

    @Auditable(entityClass = BannerSetup.class, action = "DELETE")
    @Transactional
    public void delete(UUID id) {
        bannerRepo.delete(findById(id));
    }

    public List<BannerSetup> getTrashed() {
        return bannerRepo.findAllTrashed();
    }

    @Transactional
    public void restore(UUID id) {
        bannerRepo.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        bannerRepo.permanentDelete(id.toString());
    }

    @Transactional
    public void incrementRedirection(UUID id) {
        BannerSetup banner = findById(id);
        banner.setTotalRedirection(banner.getTotalRedirection().add(java.math.BigDecimal.ONE));
        bannerRepo.save(banner);
    }
}
