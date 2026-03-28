package com.drivemond.review.controller.api;

import com.drivemond.auth.entity.User;
import com.drivemond.review.dto.ReviewRequest;
import com.drivemond.review.entity.Review;
import com.drivemond.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewApiController {

    private final ReviewService reviewService;

    /**
     * Get reviews received by the authenticated user.
     */
    @GetMapping
    public ResponseEntity<Page<Review>> list(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(reviewService.getByUser(user.getId(), true, page, size));
    }

    /**
     * Submit a review for a trip. The caller must supply the other party's ID
     * (receivedBy) and tripType — these come from the trip-management context.
     */
    @PostMapping
    public ResponseEntity<?> store(
            @AuthenticationPrincipal User user,
            @RequestParam UUID tripRequestId,
            @RequestParam String tripType,
            @RequestParam UUID receivedBy,
            @Valid @RequestBody ReviewRequest req) {
        reviewService.store(user, tripRequestId, tripType, receivedBy, req);
        return ResponseEntity.ok(Map.of("message", "Review submitted successfully"));
    }

    /**
     * Check whether the authenticated user has already reviewed a trip.
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkSubmission(
            @AuthenticationPrincipal User user,
            @RequestParam UUID tripRequestId) {
        boolean reviewed = reviewService.hasReviewed(tripRequestId, user.getId());
        return ResponseEntity.ok(Map.of("reviewed", reviewed));
    }

    /**
     * Toggle the saved/bookmarked state of a review (receiver only).
     */
    @PutMapping("/{id}/save")
    public ResponseEntity<?> save(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        reviewService.toggleSave(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "Review save status updated"));
    }
}
