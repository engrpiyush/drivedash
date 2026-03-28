package com.drivedash.core.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Injects shared model attributes into every controller response so that
 * Thymeleaf fragments (e.g. the sidebar) can reference them without relying
 * on the {@code #request} expression object, which was removed in
 * Thymeleaf 3.1 for security reasons.
 *
 * <p>Attributes added:
 * <ul>
 *   <li>{@code currentUri} – the servlet request URI (e.g. {@code /admin/customers}),
 *       used by the sidebar to highlight the active nav link and auto-expand
 *       the correct collapse section.</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalModelAdvice {

    /**
     * Exposes the current request URI as {@code currentUri} in every model.
     * Replaces the Thymeleaf 3.1-removed {@code ${#request.requestURI}} usage.
     */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
