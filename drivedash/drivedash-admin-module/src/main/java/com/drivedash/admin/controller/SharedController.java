package com.drivedash.admin.controller;

import com.drivedash.admin.dto.NotificationDto;
import com.drivedash.admin.service.AdminNotificationService;
import com.drivedash.business.entity.SettingsType;
import com.drivedash.business.service.BusinessSettingService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Shared utility endpoints – notification dropdown and locale switching.
 * Mirrors Laravel's {@code SharedController}.
 *
 * <p>Routes:
 * <ul>
 *   <li>GET /admin/get-notifications – JSON list of unseen notifications</li>
 *   <li>GET /admin/seen-notification  – marks a notification as seen</li>
 *   <li>GET /lang/{locale}            – switches the UI language</li>
 * </ul>
 */
@Controller
@RequiredArgsConstructor
public class SharedController {

    private final AdminNotificationService notificationService;
    private final BusinessSettingService businessSettingService;

    /**
     * Returns unseen admin notifications as JSON for the top-bar dropdown.
     * Called every few seconds by the admin panel's JS poller.
     */
    @GetMapping("/admin/get-notifications")
    @ResponseBody
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        return ResponseEntity.ok(notificationService.getUnseenForDropdown());
    }

    /**
     * Marks a single notification (or all if {@code id == 0}) as seen.
     */
    @GetMapping("/admin/seen-notification")
    @ResponseBody
    public ResponseEntity<NotificationDto> seenNotification(@RequestParam Long id) {
        NotificationDto dto = notificationService.markSeen(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Switches the active locale for the current session.
     * The available languages and their text direction are stored in
     * {@code business_settings} under {@code SYSTEM_LANGUAGE}.
     * Mirrors Laravel's {@code SharedController::lang($locale)}.
     */
    @GetMapping("/lang/{locale}")
    public String lang(@PathVariable String locale,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        // Read language settings to determine text direction
        Map<String, Object> langSettings = businessSettingService
                .getValue("system_language", SettingsType.SYSTEM_LANGUAGE);
        String direction = "ltr";
        if (langSettings.containsKey("direction")) {
            direction = (String) langSettings.get("direction");
        }

        // Apply locale via Spring's LocaleResolver
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver != null) {
            localeResolver.setLocale(request, response, Locale.forLanguageTag(locale));
        }

        // Store direction in session for Thymeleaf layout
        request.getSession().setAttribute("direction", direction);
        request.getSession().setAttribute("locale", locale);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/admin/dashboard");
    }
}
