package com.drivedash.review.service;

import com.drivedash.auth.entity.User;
import com.drivedash.core.exception.DrivedashException;
import com.drivedash.review.dto.ReviewRequest;
import com.drivedash.review.entity.Review;
import com.drivedash.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
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
public class ReviewService {

    private final ReviewRepository reviewRepo;

    public Page<Review> getPage(String search, Integer rating, String tripType, int page, int size) {
        Specification<Review> spec = Specification.where(null);

        if (rating != null && rating > 0) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("rating"), rating));
        }
        if (StringUtils.hasText(tripType) && !"all".equals(tripType)) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("tripType"), tripType));
        }
        if (StringUtils.hasText(search)) {
            final String like = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("feedback")), like));
        }

        return reviewRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public Page<Review> getByUser(UUID userId, boolean received, int page, int size) {
        Specification<Review> spec = received
                ? (root, q, cb) -> cb.equal(root.get("receivedBy"), userId)
                : (root, q, cb) -> cb.equal(root.get("givenBy"), userId);

        return reviewRepo.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public Review findById(Long id) {
        return reviewRepo.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Review not found"));
    }

    public double getAverageRating(UUID userId) {
        Double avg = reviewRepo.avgRatingByReceivedBy(userId);
        return avg != null ? avg : 0.0;
    }

    public long getReviewCount(UUID userId) {
        return reviewRepo.countByReceivedBy(userId);
    }

    @Transactional
    public void store(User user, UUID tripRequestId, String tripType,
                      UUID otherPartyId, ReviewRequest req) {
        if (reviewRepo.existsByTripRequestIdAndGivenBy(tripRequestId, user.getId())) {
            throw DrivedashException.conflict("Review already submitted for this trip");
        }

        reviewRepo.save(Review.builder()
                .tripRequestId(tripRequestId)
                .givenBy(user.getId())
                .receivedBy(otherPartyId)
                .tripType(tripType)
                .rating(req.getRating())
                .feedback(req.getFeedback())
                .build());
    }

    @Transactional
    public void toggleSave(Long id, UUID currentUserId) {
        Review review = findById(id);
        if (!review.getReceivedBy().equals(currentUserId)) {
            throw DrivedashException.badRequest("Cannot save a review not addressed to you");
        }
        review.setSaved(!review.isSaved());
        reviewRepo.save(review);
    }

    @Transactional
    public void delete(Long id) {
        reviewRepo.delete(findById(id));
    }

    public boolean hasReviewed(UUID tripRequestId, UUID userId) {
        return reviewRepo.existsByTripRequestIdAndGivenBy(tripRequestId, userId);
    }
}
