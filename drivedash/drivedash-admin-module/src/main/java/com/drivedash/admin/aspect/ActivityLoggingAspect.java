package com.drivedash.admin.aspect;

import com.drivedash.admin.annotation.Auditable;
import com.drivedash.admin.entity.ActivityLog;
import com.drivedash.admin.repository.ActivityLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AOP aspect that auto-records an {@link ActivityLog} entry whenever a service
 * method annotated with {@link Auditable} completes successfully.
 *
 * <p>Replaces the manual {@code ActivityLog::create([...])} calls scattered
 * across every PHP service that mutates data.
 *
 * <p>Snapshot strategy:
 * <ul>
 *   <li>The first argument of the annotated method that is a {@link UUID} is
 *       treated as the entity's ID ({@code logable_id}).</li>
 *   <li>{@code before} is set to {@code null} on CREATE actions.</li>
 *   <li>{@code after} is the serialised return value of the method (must be
 *       a DTO or entity; {@code void} methods skip the after snapshot).</li>
 * </ul>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLoggingAspect {

    private final ActivityLogRepository activityLogRepository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object logActivity(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            UUID entityId   = resolveEntityId(joinPoint.getArgs());
            UUID actorId    = resolveActorId();
            String userType = resolveUserType();

            Map<String, Object> afterSnapshot = toMap(result);

            ActivityLog log = ActivityLog.builder()
                    .logableId(entityId)
                    .logableType(auditable.entityClass().getSimpleName())
                    .editedBy(actorId)
                    .afterState(afterSnapshot)
                    .userType(userType)
                    .build();

            activityLogRepository.save(log);
        } catch (Exception ex) {
            // Logging failure must never break the primary operation
            log.warn("ActivityLoggingAspect failed to record log: {}", ex.getMessage());
        }

        return result;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private UUID resolveEntityId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }
        return null;
    }

    private UUID resolveActorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        try {
            return UUID.fromString(auth.getName());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String resolveUserType() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .map(a -> a.replace("ROLE_", "").toLowerCase())
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, Map.class);
        } catch (Exception ex) {
            return Map.of("value", obj.toString());
        }
    }
}