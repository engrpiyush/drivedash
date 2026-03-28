package com.drivedash.admin.repository;

import com.drivedash.admin.entity.AdminNotification;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {

    List<AdminNotification> findAllBySeenFalseOrderByCreatedAtDesc();

    List<AdminNotification> findAllBySeenFalseOrderByCreatedAtDesc(Pageable pageable);

    long countBySeenFalse();

    /** Bulk-marks all unseen notifications as seen – mirrors Laravel's {@code updatedBy()}. */
    @Modifying
    @Query("UPDATE AdminNotification n SET n.seen = true WHERE n.seen = false")
    int markAllSeen();
}
