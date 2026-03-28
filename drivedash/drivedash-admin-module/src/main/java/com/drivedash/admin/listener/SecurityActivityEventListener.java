package com.drivedash.admin.listener;

import com.drivedash.admin.service.ActivityLogRecorder;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Logs session-level security events.
 */
@Component
public class SecurityActivityEventListener {

    private final ActivityLogRecorder recorder;

    public SecurityActivityEventListener(ActivityLogRecorder recorder) {
        this.recorder = recorder;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        recorder.record(
                event.getAuthentication(),
                "Session",
                null,
                null,
                Map.of("action", "LOGIN"));
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        recorder.record(
                event.getAuthentication(),
                "Session",
                null,
                null,
                Map.of("action", "LOGOUT"));
    }
}
