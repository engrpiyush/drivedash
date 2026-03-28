package com.drivedash.core.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

/**
 * Published by the web security logout handler so that the admin module
 * can record an {@code activity_logs} entry without creating a circular
 * dependency between {@code drivedash-auth} and {@code drivedash-admin-module}.
 */
public class UserLogoutEvent extends ApplicationEvent {

    private final transient Authentication authentication;

    public UserLogoutEvent(Object source, Authentication authentication) {
        super(source);
        this.authentication = authentication;
    }

    /** The Spring Security {@link Authentication} of the user who logged out. */
    public Authentication getAuthentication() {
        return authentication;
    }
}
