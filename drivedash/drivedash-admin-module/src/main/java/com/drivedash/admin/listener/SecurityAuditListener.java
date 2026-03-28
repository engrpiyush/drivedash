package com.drivedash.admin.listener;

import com.drivedash.admin.entity.ActivityLog;
import com.drivedash.admin.repository.ActivityLogRepository;
import com.drivedash.auth.entity.User;
import com.drivedash.core.event.UserLogoutEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Listens for Spring Security authentication events and writes
 * {@code activity_logs} entries for LOGIN and LOGOUT actions.
 *
 * <p>Events handled:
 * <ul>
 *   <li>{@link InteractiveAuthenticationSuccessEvent} – fired by Spring Security
 *       on every successful form-login (web admin panel).</li>
 *   <li>{@link UserLogoutEvent} – a custom event published by the web security
 *       chain's {@code LogoutSuccessHandler} in {@code SecurityConfig}.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityAuditListener {

    private final ActivityLogRepository activityLogRepository;

    /**
     * Records a LOGIN entry when an admin / employee authenticates via the
     * web form-login flow.
     */
    @EventListener
    public void onLoginSuccess(InteractiveAuthenticationSuccessEvent event) {
        try {
            Authentication auth = event.getAuthentication();
            saveSessionLog(auth, "LOGIN");
        } catch (Exception ex) {
            log.warn("SecurityAuditListener: failed to persist LOGIN log: {}", ex.getMessage());
        }
    }

    /**
     * Records a LOGOUT entry published by {@code SecurityConfig}'s logout handler.
     */
    @EventListener
    public void onLogout(UserLogoutEvent event) {
        try {
            Authentication auth = event.getAuthentication();
            if (auth != null) {
                saveSessionLog(auth, "LOGOUT");
            }
        } catch (Exception ex) {
            log.warn("SecurityAuditListener: failed to persist LOGOUT log: {}", ex.getMessage());
        }
    }

    // ── Internal helper ────────────────────────────────────────────────────

    private void saveSessionLog(Authentication auth, String action) {
        UUID actorId   = resolveUserId(auth);
        String userType = resolveUserType(auth);
        String name     = auth != null ? auth.getName() : "unknown";

        ActivityLog entry = ActivityLog.builder()
                .logableId(actorId)           // the user IS the logable entity for session events
                .logableType("User")
                .action(action)
                .editedBy(actorId)
                .userType(userType)
                .build();

        activityLogRepository.save(entry);
        log.debug("SecurityAuditListener: recorded {} for user '{}'", action, name);
    }

    private UUID resolveUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }
        return null;
    }

    private String resolveUserType(Authentication auth) {
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
}
