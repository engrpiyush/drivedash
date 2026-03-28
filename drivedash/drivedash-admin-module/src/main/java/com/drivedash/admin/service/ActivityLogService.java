package com.drivedash.admin.service;

import com.drivedash.admin.dto.ActivityLogFilterRequest;
import com.drivedash.admin.entity.ActivityLog;
import com.drivedash.admin.repository.ActivityLogRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Query service for the activity log.
 * Write operations are handled exclusively by
 * {@link com.drivedash.admin.aspect.ActivityLoggingAspect} –
 * no manual {@code save()} calls are needed in service code.
 */
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;

    @Transactional(readOnly = true)
    public Page<ActivityLog> findLogs(ActivityLogFilterRequest filter) {
        PageRequest pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        String type     = filter.getLogableType();
        UUID   id       = filter.getLogableId();
        String userType = filter.getUserType();

        if (id != null && userType != null) {
            return repository.findAllByLogableTypeAndLogableIdAndUserType(
                    type, id, userType, pageable);
        }
        if (id != null) {
            return repository.findAllByLogableTypeAndLogableId(type, id, pageable);
        }
        if (userType != null) {
            return repository.findAllByLogableTypeAndUserType(type, userType, pageable);
        }
        return repository.findAllByLogableType(type, pageable);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return repository.count();
    }
}