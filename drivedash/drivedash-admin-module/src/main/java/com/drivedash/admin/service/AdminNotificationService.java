package com.drivedash.admin.service;

import com.drivedash.admin.dto.NotificationDto;
import com.drivedash.admin.entity.AdminNotification;
import com.drivedash.admin.repository.AdminNotificationRepository;
import com.drivedash.core.exception.DrivedashException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages admin-panel in-app notifications.
 * Mirrors Laravel's {@code AdminNotificationService}.
 */
@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private static final int DROPDOWN_LIMIT = 10;

    private final AdminNotificationRepository repository;

    // ── Write ───────────────────────────────────────────────────────────────

    /**
     * Creates a new admin notification.
     * Called by other modules (trip accepted, new driver registered, etc.)
     * using Spring application events.
     */
    @Transactional
    public AdminNotification create(String model, UUID modelId, String message) {
        return repository.save(AdminNotification.builder()
                .model(model)
                .modelId(modelId)
                .message(message)
                .seen(false)
                .build());
    }

    /**
     * Marks a single notification as seen, or all notifications if {@code id == 0}.
     * Mirrors Laravel's {@code AdminNotificationService::update(id, ['is_seen'=>true])}.
     */
    @Transactional
    public NotificationDto markSeen(Long id) {
        if (id == 0L) {
            repository.markAllSeen();
            return null;
        }
        AdminNotification notification = repository.findById(id)
                .orElseThrow(() -> DrivedashException.notFound("Notification not found"));
        notification.setSeen(true);
        return toDto(repository.save(notification));
    }

    // ── Read ────────────────────────────────────────────────────────────────

    /** Returns the most recent unseen notifications for the dropdown (capped at 10). */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnseenForDropdown() {
        return repository
                .findAllBySeenFalseOrderByCreatedAtDesc(PageRequest.of(0, DROPDOWN_LIMIT))
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countUnseen() {
        return repository.countBySeenFalse();
    }

    // ── Mapping ─────────────────────────────────────────────────────────────

    private NotificationDto toDto(AdminNotification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .model(n.getModel())
                .modelId(n.getModelId())
                .message(n.getMessage())
                .seen(n.isSeen())
                .createdAt(n.getCreatedAt())
                .build();
    }
}