package com.drivedash.admin.aspect;

import com.drivedash.admin.service.ActivityLogRecorder;
import java.lang.reflect.Method;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Captures mutation activity for service-layer write operations and stores
 * entity snapshots (before/after) for diff-friendly activity logs.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLoggingAspect {

    private final ActivityLogRecorder recorder;

    @Around(
            "execution(* com.drivedash..service..*.create*(..)) || " +
            "execution(* com.drivedash..service..*.update*(..)) || " +
            "execution(* com.drivedash..service..*.delete*(..)) || " +
            "execution(* com.drivedash..service..*.toggle*(..)) || " +
            "execution(* com.drivedash..service..*.approve*(..)) || " +
            "execution(* com.drivedash..service..*.process*(..)) || " +
            "execution(* com.drivedash..service..*.store*(..))"
    )
    public Object logMutation(ProceedingJoinPoint joinPoint) throws Throwable {
        UUID idArg = resolveUuidArg(joinPoint.getArgs());
        Object before = tryLoadCurrentState(joinPoint.getTarget(), idArg);
        String methodName = joinPoint.getSignature().getName().toLowerCase();

        Object result = joinPoint.proceed();

        Object after = null;
        if (!methodName.startsWith("delete")) {
            after = result != null ? unwrapResponseBody(result) : tryLoadCurrentState(joinPoint.getTarget(), idArg);
        }

        UUID entityId = resolveEntityId(idArg, before, after, result);
        String logableType = resolveLogableType(before, after, joinPoint.getTarget().getClass().getSimpleName());
        recorder.recordForCurrentUser(logableType, entityId, before, after);
        return result;
    }

    private UUID resolveEntityId(UUID idArg, Object before, Object after, Object result) {
        if (idArg != null) {
            return idArg;
        }
        UUID fromBefore = tryResolveFromObject(before);
        if (fromBefore != null) {
            return fromBefore;
        }
        UUID fromAfter = tryResolveFromObject(after);
        if (fromAfter != null) {
            return fromAfter;
        }
        return tryResolveFromObject(unwrapResponseBody(result));
    }

    private UUID resolveUuidArg(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }
        return null;
    }

    private Object tryLoadCurrentState(Object target, UUID id) {
        if (id == null || target == null) {
            return null;
        }
        try {
            Method m = target.getClass().getMethod("findById", UUID.class);
            return m.invoke(target, id);
        } catch (Exception ignored) {
            return null;
        }
    }

    private UUID tryResolveFromObject(Object candidate) {
        if (candidate == null) {
            return null;
        }
        try {
            Method getId = candidate.getClass().getMethod("getId");
            Object id = getId.invoke(candidate);
            if (id instanceof UUID uuid) {
                return uuid;
            }
            if (id instanceof String str) {
                return UUID.fromString(str);
            }
        } catch (Exception ignored) {
            // best-effort only
        }
        return null;
    }

    private String resolveLogableType(Object before, Object after, String fallbackSource) {
        Object sample = after != null ? after : before;
        if (sample != null) {
            String type = sample.getClass().getSimpleName();
            if (!type.isBlank()) {
                return type;
            }
        }
        String type = fallbackSource;
        if (type.endsWith("Service")) {
            type = type.substring(0, type.length() - "Service".length());
        }
        return type;
    }

    private Object unwrapResponseBody(Object result) {
        if (result instanceof org.springframework.http.ResponseEntity<?> response) {
            return response.getBody();
        }
        return result;
    }
}
