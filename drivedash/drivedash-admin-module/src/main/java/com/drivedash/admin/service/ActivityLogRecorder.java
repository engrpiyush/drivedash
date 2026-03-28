package com.drivedash.admin.service;

import com.drivedash.admin.entity.ActivityLog;
import com.drivedash.admin.repository.ActivityLogRepository;
import com.drivedash.auth.entity.User;
import com.drivedash.auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Centralized write path for activity logs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogRecorder {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public void recordForCurrentUser(String logableType, UUID logableId, Object before, Object after) {
        record(SecurityContextHolder.getContext().getAuthentication(), logableType, logableId, before, after);
    }

    public void record(Authentication auth, String logableType, UUID logableId, Object before, Object after) {
        try {
            Actor actor = resolveActor(auth);
            if (actor == null || actor.userId() == null) {
                return;
            }

            UUID resolvedLogableId = logableId != null ? logableId : actor.userId();
            String resolvedType = (logableType == null || logableType.isBlank()) ? "Unknown" : logableType;

            ActivityLog logEntry = ActivityLog.builder()
                    .logableId(resolvedLogableId)
                    .logableType(resolvedType)
                    .editedBy(actor.userId())
                    .beforeState(toMap(before))
                    .afterState(toMap(after))
                    .userType(actor.userType())
                    .build();

            activityLogRepository.save(logEntry);
        } catch (Exception ex) {
            // Activity logging must never block the primary request path.
            log.warn("Failed to persist activity log: {}", ex.getMessage());
        }
    }

    private Actor resolveActor(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return new Actor(user.getId(), normalizeUserType(user.getUserType() != null ? user.getUserType().name() : null));
        }

        if (principal instanceof UserDetails details) {
            User user = userRepository.findByEmail(details.getUsername())
                    .or(() -> userRepository.findByPhone(details.getUsername()))
                    .orElse(null);
            if (user != null) {
                return new Actor(user.getId(), normalizeUserType(user.getUserType() != null ? user.getUserType().name() : null));
            }
        }

        String name = auth.getName();
        User byName = userRepository.findByEmail(name).or(() -> userRepository.findByPhone(name)).orElse(null);
        if (byName != null) {
            return new Actor(byName.getId(), normalizeUserType(byName.getUserType() != null ? byName.getUserType().name() : null));
        }
        try {
            return new Actor(UUID.fromString(name), resolveRoleType(auth));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String resolveRoleType(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .map(a -> a.replace("ROLE_", "").toLowerCase())
                .orElse("unknown");
    }

    private String normalizeUserType(String userType) {
        return userType == null ? "unknown" : userType.toLowerCase();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, Map.class);
        } catch (Exception ex) {
            return Map.of("value", String.valueOf(obj));
        }
    }

    private record Actor(UUID userId, String userType) {
    }
}
