package com.drivedash.promotion.controller.api;

import com.drivedash.auth.entity.User;
import com.drivedash.promotion.entity.BannerSetup;
import com.drivedash.promotion.entity.CouponSetup;
import com.drivedash.promotion.service.BannerSetupService;
import com.drivedash.promotion.service.CouponSetupService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PromotionApiController {

    private final BannerSetupService bannerService;
    private final CouponSetupService couponService;

    @GetMapping("/banners")
    public ResponseEntity<List<BannerSetup>> getBanners() {
        return ResponseEntity.ok(bannerService.getActiveList());
    }

    @PostMapping("/banners/{id}/redirection")
    public ResponseEntity<Void> trackRedirection(@PathVariable UUID id) {
        bannerService.incrementRedirection(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<CouponSetup>> getCoupons(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                couponService.getActiveCouponsForUser(user.getId(), user.getUserLevelId()));
    }
}
