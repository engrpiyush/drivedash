package com.drivedash.admin.aspect;

import com.drivedash.admin.service.ActivityLogRecorder;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Captures mutation activity for controller endpoints (POST/PUT/PATCH/DELETE).
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ActivityLoggingAspect {

    private final ActivityLogRecorder recorder;

    @Around("(@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController))"
            + " && ("
            + "@annotation(org.springframework.web.bind.annotation.PostMapping)"
            + " || @annotation(org.springframework.web.bind.annotation.PutMapping)"
            + " || @annotation(org.springframework.web.bind.annotation.PatchMapping)"
            + " || @annotation(org.springframework.web.bind.annotation.DeleteMapping)"
            + ")")
    public Object logMutation(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;

        Map<String, Object> before = new LinkedHashMap<>();
        before.put("method", request != null ? request.getMethod() : null);
        before.put("path", request != null ? request.getRequestURI() : null);
        before.put("operation", joinPoint.getSignature().toShortString());

        Object result = joinPoint.proceed();

        Map<String, Object> after = new LinkedHashMap<>();
        after.put("method", request != null ? request.getMethod() : null);
        after.put("path", request != null ? request.getRequestURI() : null);
        after.put("result", summarize(result));

        UUID entityId = resolveEntityId(joinPoint.getArgs(), result);
        String logableType = resolveLogableType(joinPoint, request);
        recorder.recordForCurrentUser(logableType, entityId, before, after);
        return result;
    }

    private UUID resolveEntityId(Object[] args, Object result) {
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }
        UUID fromResult = tryResolveFromObject(result);
        if (fromResult != null) {
            return fromResult;
        }
        if (result instanceof org.springframework.http.ResponseEntity<?> response) {
            return tryResolveFromObject(response.getBody());
        }
        return null;
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

    private String resolveLogableType(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        if (controllerName.endsWith("Controller")) {
            controllerName = controllerName.substring(0, controllerName.length() - "Controller".length());
        }
        if (controllerName.isBlank() && request != null) {
            return request.getRequestURI();
        }
        return controllerName;
    }

    private Object summarize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence || value instanceof Number || value instanceof Boolean) {
            return value;
        }
        return value.getClass().getSimpleName();
    }
}
