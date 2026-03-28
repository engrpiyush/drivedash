package com.drivedash.admin.aspect;

import com.drivedash.admin.entity.ActivityLog;
import com.drivedash.admin.repository.ActivityLogRepository;
import com.drivedash.auth.entity.User;
import com.drivedash.core.annotation.Auditable;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AOP aspect that auto-records an {@link ActivityLog} entry whenever a service
 * method annotated with {@link Auditable} completes successfully.
 *
 * <p>Entity ID resolution strategy (in priority order):
 * <ol>
 *   <li>First {@link UUID} argument → used for UPDATE / DELETE / STATUS_CHANGE</li>
 *   <li>Return value's {@code getId()} method → used for CREATE operations
 *       where the ID is assigned by the database after {@code save()}</li>
 *   <li>{@code null} → logged without a logable_id (acceptable for batch ops)</li>
 * </ol>
 *
 * <p>Actor ID resolution: casts {@code auth.getPrincipal()} to {@link User}
 * and reads {@code user.getId()}.  Falls back gracefully to {@code null} if the
 * principal is not a {@link User} (e.g. anonymous, JWT-only paths).
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
            // For CREATE the entity UUID is in the return value; for UPDATE/DELETE it's the first arg
            UUID entityId = resolveEntityId(joinPoint.getArgs(), result, auditable.action());
            UUID actorId  = resolveActorId();
            String userType = resolveUserType();

            Map<String, Object> afterSnapshot = toMap(result);

            ActivityLog entry = ActivityLog.builder()
                    .logableId(entityId)
                    .logableType(auditable.entityClass().getSimpleName())
                    .action(auditable.action())
                    .editedBy(actorId)
                    .afterState(afterSnapshot)
                    .userType(userType)
                    .build();

            activityLogRepository.save(entry);
        } catch (Exception ex) {
            // Logging failure must NEVER break the primary operation
            log.warn("ActivityLoggingAspect: failed to persist log entry [{}#{}]: {}",
                    auditable.entityClass().getSimpleName(),
                    auditable.action(),
                    ex.getMessage());
        }

        return result;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /**
     * For UPDATE / DELETE / STATUS_CHANGE: the first {@link UUID} in the argument
     * list is the entity ID passed by the caller.
     *
     * For CREATE: the entity does not have an ID until after {@code save()}, so we
     * extract it from the return value via reflection ({@code result.getId()}).
     */
    private UUID resolveEntityId(Object[] args, Object result, String action) {
        // Try arguments first (covers UPDATE / DELETE where UUID id is the first param)
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }

        // For CREATE-style operations, extract the ID from the saved entity return value
        if (result != null) {
            try {
                Method getId = result.getClass().getMethod("getId");
                Object idVal = getId.invoke(result);
                if (idVal instanceof UUID uuid) {
                    return uuid;
                }
            } catch (Exception ignored) {
                // Return value has no getId() – that's fine (e.g. void or primitive)
            }
        }

        return null;
    }

    /**
     * Extracts the actor's UUID by casting the Security principal to {@link User}.
     * Falls back to {@code null} for anonymous or non-User principals.
     */
    private UUID resolveActorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }
        return null;
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
