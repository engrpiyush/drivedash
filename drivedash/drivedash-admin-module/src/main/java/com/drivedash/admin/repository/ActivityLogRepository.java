package com.drivedash.admin.repository;

import com.drivedash.admin.entity.ActivityLog;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link ActivityLog}.
 * {@link JpaSpecificationExecutor} supports the dynamic filtering
 * (logable_type, logable_id, user_type) from the PHP service.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>,
        JpaSpecificationExecutor<ActivityLog> {

    Page<ActivityLog> findAllByLogableType(String logableType, Pageable pageable);

    Page<ActivityLog> findAllByLogableTypeAndLogableId(String logableType, UUID logableId,
            Pageable pageable);

    Page<ActivityLog> findAllByLogableTypeAndUserType(String logableType, String userType,
            Pageable pageable);

    Page<ActivityLog> findAllByLogableTypeAndLogableIdAndUserType(String logableType,
            UUID logableId, String userType, Pageable pageable);
}