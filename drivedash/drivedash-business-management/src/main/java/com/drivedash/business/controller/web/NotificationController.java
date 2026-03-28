package com.drivedash.business.controller.web;

import com.drivedash.business.entity.NotificationSetting;
import com.drivedash.business.service.NotificationSettingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Push / email notification toggle settings.
 * Routes: /admin/business/configuration/notification/**
 */
@Controller
@RequestMapping("/admin/business/configuration/notification")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("notificationSettings", notificationSettingService.findAll());
        return "admin/business/configuration/notification";
    }

    @PostMapping("/store")
    public String store(@RequestBody List<NotificationSetting> settings,
                        RedirectAttributes redirect) {
        notificationSettingService.batchUpsert(settings);
        redirect.addFlashAttribute("success", "Notification settings saved.");
        return "redirect:/admin/business/configuration/notification";
    }
}
