package com.drivemond.promotion.service;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.promotion.dto.CouponSetupRequest;
import com.drivemond.promotion.entity.CouponSetup;
import com.drivemond.promotion.repository.CouponSetupRepository;
import com.drivemond.usermanagement.entity.UserLevel;
import com.drivemond.usermanagement.repository.UserLevelRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
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
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CouponSetupService {

    private final CouponSetupRepository couponRepo;
    private final UserLevelRepository userLevelRepo;

    public Page<CouponSetup> getPage(String search, String status, int page, int size) {
        Specification<CouponSetup> spec = Specification.where(null);

        if ("active".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isTrue(root.get("isActive")));
        } else if ("inactive".equals(status)) {
            spec = spec.and((root, q, cb) -> cb.isFalse(root.get("isActive")));
        }
        if (StringUtils.hasText(search)) {
            final String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("couponCode")), like)
            ));
        }

        return couponRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public CouponSetup findById(UUID id) {
        return couponRepo.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Coupon not found"));
    }

    public Optional<CouponSetup> findByCouponCode(String code) {
        return couponRepo.findByCouponCode(code);
    }

    public List<UserLevel> getUserLevels() {
        return userLevelRepo.findAll(Sort.by("sequence"));
    }

    @Transactional
    public void create(CouponSetupRequest req) {
        if (couponRepo.existsByCouponCode(req.getCouponCode())) {
            throw DrivemondException.conflict("Coupon code already exists");
        }

        BigDecimal minTripAmount = "first_order".equals(req.getCouponType())
                ? BigDecimal.ZERO : req.getMinTripAmount();

        couponRepo.save(CouponSetup.builder()
                .name(req.getName())
                .description(req.getDescription())
                .couponCode(req.getCouponCode())
                .userId(req.getUserId())
                .userLevelId(req.getUserLevelId())
                .coupon(req.getCoupon())
                .amountType(req.getAmountType())
                .couponType(req.getCouponType())
                .limit(req.getLimit())
                .minTripAmount(minTripAmount)
                .maxCouponAmount(req.getMaxCouponAmount() != null
                        ? req.getMaxCouponAmount() : BigDecimal.ZERO)
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .rules(req.getRules())
                .vehicleCategoryIds(req.getVehicleCategoryIds())
                .build());
    }

    @Transactional
    public void update(UUID id, CouponSetupRequest req) {
        CouponSetup coupon = findById(id);

        BigDecimal minTripAmount = "first_order".equals(req.getCouponType())
                ? BigDecimal.ZERO : req.getMinTripAmount();

        coupon.setName(req.getName());
        coupon.setDescription(req.getDescription());
        coupon.setCoupon(req.getCoupon());
        coupon.setAmountType(req.getAmountType());
        coupon.setCouponType(req.getCouponType());
        coupon.setLimit(req.getLimit());
        coupon.setMinTripAmount(minTripAmount);
        coupon.setMaxCouponAmount(req.getMaxCouponAmount() != null
                ? req.getMaxCouponAmount() : BigDecimal.ZERO);
        coupon.setStartDate(req.getStartDate());
        coupon.setEndDate(req.getEndDate());
        coupon.setRules(req.getRules());
        coupon.setVehicleCategoryIds(req.getVehicleCategoryIds());
        couponRepo.save(coupon);
    }

    public void toggleStatus(UUID id, boolean active) {
        CouponSetup coupon = findById(id);
        coupon.setActive(active);
        couponRepo.save(coupon);
    }

    @Transactional
    public void delete(UUID id) {
        couponRepo.delete(findById(id));
    }

    public List<CouponSetup> getTrashed() {
        return couponRepo.findAllTrashed();
    }

    @Transactional
    public void restore(UUID id) {
        couponRepo.restore(id.toString());
    }

    @Transactional
    public void permanentDelete(UUID id) {
        couponRepo.permanentDelete(id.toString());
    }

    public Map<String, Object> getCardValues() {
        LocalDate today = LocalDate.now();
        Map<String, Object> cards = new HashMap<>();
        cards.put("totalActive", couponRepo.countByIsActiveTrue());
        cards.put("totalInactive", couponRepo.countByIsActiveFalse());

        BigDecimal[] totals = couponRepo.findAll().stream()
                .reduce(new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO},
                        (acc, c) -> new BigDecimal[]{
                                acc[0].add(c.getTotalAmount()),
                                acc[1].add(c.getTotalUsed())
                        },
                        (a, b) -> new BigDecimal[]{a[0].add(b[0]), a[1].add(b[1])});

        cards.put("totalCouponAmount", totals[0]);
        cards.put("totalUsed", totals[1]);
        return cards;
    }

    public List<CouponSetup> getActiveCouponsForUser(UUID userId, UUID userLevelId) {
        LocalDate today = LocalDate.now();
        Specification<CouponSetup> spec = (root, q, cb) -> cb.and(
                cb.isTrue(root.get("isActive")),
                cb.or(
                        cb.isNull(root.get("startDate")),
                        cb.lessThanOrEqualTo(root.get("startDate"), today)
                ),
                cb.or(
                        cb.isNull(root.get("endDate")),
                        cb.greaterThanOrEqualTo(root.get("endDate"), today)
                ),
                cb.or(
                        cb.isNull(root.get("userId")),
                        cb.equal(root.get("userId"), userId)
                )
        );
        return couponRepo.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
