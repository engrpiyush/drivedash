package com.drivemond.usermanagement.service;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.core.util.FileStorageService;
import com.drivemond.usermanagement.dto.UserLevelRequest;
import com.drivemond.usermanagement.entity.LevelAccess;
import com.drivemond.usermanagement.entity.UserLevel;
import com.drivemond.usermanagement.repository.LevelAccessRepository;
import com.drivemond.usermanagement.repository.UserLevelRepository;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserLevelService {

    private final UserLevelRepository levelRepository;
    private final LevelAccessRepository accessRepository;
    private final FileStorageService fileStorage;

    public Page<UserLevel> getPage(String userType, String status, int page, int size) {
        Specification<UserLevel> spec = Specification
                .where(typeSpec(userType))
                .and(statusSpec(status));
        return levelRepository.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sequence")));
    }

    public List<UserLevel> getActiveByType(String userType) {
        return levelRepository.findAllByUserTypeAndActiveTrueOrderBySequenceAsc(userType);
    }

    public UserLevel findById(UUID id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Level not found"));
    }

    @Transactional
    public UserLevel create(UserLevelRequest req) {
        if (levelRepository.existsByNameAndUserType(req.getName(), req.getUserType())) {
            throw DrivemondException.conflict("Level name already exists for this user type");
        }
        String image = storeImage(req.getImageFile(), null);
        UserLevel level = UserLevel.builder()
                .sequence(req.getSequence())
                .name(req.getName())
                .userType(req.getUserType())
                .rewardType(req.getRewardType())
                .rewardAmount(req.getRewardAmount())
                .image(image)
                .targetedRide(req.getTargetedRide())
                .targetedRidePoint(req.getTargetedRidePoint())
                .targetedAmount(req.getTargetedAmount())
                .targetedAmountPoint(req.getTargetedAmountPoint())
                .targetedCancel(req.getTargetedCancel())
                .targetedCancelPoint(req.getTargetedCancelPoint())
                .targetedReview(req.getTargetedReview())
                .targetedReviewPoint(req.getTargetedReviewPoint())
                .build();
        level = levelRepository.save(level);
        saveAccess(level.getId(), req);
        return level;
    }

    @Transactional
    public UserLevel update(UUID id, UserLevelRequest req) {
        UserLevel level = findById(id);
        if (levelRepository.existsByNameAndUserTypeAndIdNot(req.getName(), req.getUserType(), id)) {
            throw DrivemondException.conflict("Level name already exists for this user type");
        }
        String image = storeImage(req.getImageFile(), level.getImage());
        level.setSequence(req.getSequence());
        level.setName(req.getName());
        level.setRewardType(req.getRewardType());
        level.setRewardAmount(req.getRewardAmount());
        level.setImage(image);
        level.setTargetedRide(req.getTargetedRide());
        level.setTargetedRidePoint(req.getTargetedRidePoint());
        level.setTargetedAmount(req.getTargetedAmount());
        level.setTargetedAmountPoint(req.getTargetedAmountPoint());
        level.setTargetedCancel(req.getTargetedCancel());
        level.setTargetedCancelPoint(req.getTargetedCancelPoint());
        level.setTargetedReview(req.getTargetedReview());
        level.setTargetedReviewPoint(req.getTargetedReviewPoint());
        level = levelRepository.save(level);
        saveAccess(id, req);
        return level;
    }

    @Transactional
    public void delete(UUID id) {
        levelRepository.delete(findById(id));
    }

    @Transactional
    public void toggleStatus(UUID id, boolean active) {
        UserLevel level = findById(id);
        level.setActive(active);
        levelRepository.save(level);
    }

    public Page<UserLevel> getTrashed(int page, int size) {
        return levelRepository.findAllTrashed(PageRequest.of(page, size));
    }

    @Transactional
    public void restore(UUID id) {
        levelRepository.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        levelRepository.permanentDelete(id.toString());
    }

    public LevelAccess getAccess(UUID levelId, String userType) {
        return accessRepository.findByLevelIdAndUserType(levelId, userType)
                .orElse(LevelAccess.builder().levelId(levelId).userType(userType).build());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void saveAccess(UUID levelId, UserLevelRequest req) {
        LevelAccess access = accessRepository.findByLevelIdAndUserType(levelId, req.getUserType())
                .orElse(LevelAccess.builder().levelId(levelId).userType(req.getUserType()).build());
        access.setBid(req.isBid());
        access.setSeeDestination(req.isSeeDestination());
        access.setSeeSubtotal(req.isSeeSubtotal());
        access.setSeeLevel(req.isSeeLevel());
        access.setCreateHireRequest(req.isCreateHireRequest());
        accessRepository.save(access);
    }

    private String storeImage(MultipartFile file, String existing) {
        if (file != null && !file.isEmpty()) {
            return fileStorage.store(file, "user-level");
        }
        return existing;
    }

    // ── Specifications ────────────────────────────────────────────────────────

    private Specification<UserLevel> typeSpec(String userType) {
        if (!StringUtils.hasText(userType) || "all".equals(userType)) return null;
        return (root, q, cb) -> cb.equal(root.get("userType"), userType);
    }

    private Specification<UserLevel> statusSpec(String status) {
        if ("active".equals(status)) return (r, q, cb) -> cb.isTrue(r.get("active"));
        if ("inactive".equals(status)) return (r, q, cb) -> cb.isFalse(r.get("active"));
        return null;
    }
}
