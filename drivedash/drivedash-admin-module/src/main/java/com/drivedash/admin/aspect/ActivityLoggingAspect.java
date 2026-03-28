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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AOP aspect that auto-records an {@link ActivityLog} entry whenever a service
 * method annotated with {@link Auditable} completes successfully.
 *
 * <h3>Snapshot strategy</h3>
 * <ul>
 *   <li><b>beforeState</b> – loaded via {@link BeforeStateLoader} <em>before</em>
 *       {@code proceed()} is called, so it reflects the entity state prior to
 *       the mutation.  Always {@code null} for {@code CREATE} actions.</li>
 *   <li><b>afterState</b> – the serialised return value of the annotated method.
 *       {@code void} methods produce a {@code null} after-state.</li>
 * </ul>
 *
 * <h3>Entity ID resolution (in priority order)</h3>
 * <ol>
 *   <li>First {@link UUID} argument – used for UPDATE / DELETE / STATUS_CHANGE</li>
 *   <li>Return value's {@code getId()} – used for CREATE where the DB assigns the ID</li>
 *   <li>{@code null} – logged without a logable_id (acceptable for batch ops)</li>
 * </ol>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLoggingAspect {

    private final ActivityLogRepository activityLogRepository;
    private final BeforeStateLoader beforeStateLoader;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object logActivity(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {

        // ── 1. Capture BEFORE state ───────────────────────────────────────────
        // For CREATE there is no existing entity to snapshot; skip the DB lookup.
        Map<String, Object> beforeSnapshot = null;
        if (!"CREATE".equals(auditable.action())) {
            UUID preId = extractUuidFromArgs(joinPoint.getArgs());
            if (preId != null) {
                beforeSnapshot = beforeStateLoader.loadSnapshot(auditable.entityClass(), preId);
            }
        }

        // ── 2. Execute the real service method ────────────────────────────────
        Object result = joinPoint.proceed();

        // ── 3. Build and persist the log entry ────────────────────────────────
        try {
            // For CREATE, the UUID comes from the saved entity's getId()
            UUID entityId   = resolveEntityId(joinPoint.getArgs(), result);
            UUID actorId    = resolveActorId();
            String userType = resolveUserType();

            Map<String, Object> afterSnapshot = toMap(result);

            ActivityLog entry = ActivityLog.builder()
                    .logableId(entityId)
                    .logableType(auditable.entityClass().getSimpleName())
                    .action(auditable.action())
                    .editedBy(actorId)
                    .beforeState(beforeSnapshot)
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
     * Returns the first {@link UUID} found in {@code args} (covers UPDATE/DELETE
     * where the entity ID is the first parameter), or falls back to extracting
     * the ID from the return value via reflection (covers CREATE).
     */
    private UUID resolveEntityId(Object[] args, Object result) {
        // Primary: first UUID arg (UPDATE / DELETE / STATUS_CHANGE)
        UUID fromArgs = extractUuidFromArgs(args);
        if (fromArgs != null) {
            return fromArgs;
        }
        // Fallback: entity returned by a CREATE method
        if (result != null) {
            try {
                Method getId = result.getClass().getMethod("getId");
                Object idVal = getId.invoke(result);
                if (idVal instanceof UUID uuid) {
                    return uuid;
                }
            } catch (Exception ignored) {
                // void return or entity without getId() — acceptable
            }
        }
        return null;
    }

    /** Returns the first {@link UUID} found in the argument list, or {@code null}. */
    private UUID extractUuidFromArgs(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }
        return null;
    }

    /**
     * Extracts the actor's UUID by casting the Security principal to {@link User}.
     * Returns {@code null} for anonymous or non-User principals.
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
