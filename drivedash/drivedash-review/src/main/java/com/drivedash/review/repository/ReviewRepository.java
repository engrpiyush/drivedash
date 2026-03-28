package com.drivedash.review.repository;

import com.drivedash.review.entity.Review;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>,
        JpaSpecificationExecutor<Review> {

    Optional<Review> findByTripRequestIdAndGivenBy(UUID tripRequestId, UUID givenBy);

    boolean existsByTripRequestIdAndGivenBy(UUID tripRequestId, UUID givenBy);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.receivedBy = :userId")
    Double avgRatingByReceivedBy(@Param("userId") UUID userId);

    long countByReceivedBy(UUID userId);
}
