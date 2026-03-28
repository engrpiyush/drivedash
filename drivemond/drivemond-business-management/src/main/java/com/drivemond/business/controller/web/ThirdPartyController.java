package com.drivemond.business.controller.web;

import com.drivemond.business.entity.SettingsType;
import com.drivemond.business.service.BusinessSettingService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Third-party configuration: email, recaptcha, Google Map API.
 * Routes: /admin/business/configuration/third-party/**
 */
@Controller
@RequestMapping("/admin/business/configuration/third-party")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class ThirdPartyController {

    private final BusinessSettingService businessSettingService;

    // ── Email config ────────────────────────────────────────────────────────

    @GetMapping("/email-config")
    public String emailConfig(Model model) {
        model.addAttribute("emailConfig",
                businessSettingService.getValue("email_config", SettingsType.EMAIL_CONFIG));
        return "admin/business/configuration/email-config";
    }

    @PostMapping("/email-config/update")
    public String updateEmailConfig(@RequestParam Map<String, Object> data,
                                    RedirectAttributes redirect) {
        businessSettingService.storeEmailConfig(data);
        redirect.addFlashAttribute("success", "Email configuration updated.");
        return "redirect:/admin/business/configuration/third-party/email-config";
    }

    // ── reCAPTCHA ───────────────────────────────────────────────────────────

    @GetMapping("/recaptcha")
    public String recaptcha(Model model) {
        model.addAttribute("recaptcha",
                businessSettingService.getValue("recaptcha", SettingsType.RECAPTCHA));
        return "admin/business/configuration/recapcha";
    }

    @PostMapping("/recaptcha/update")
    public String updateRecaptcha(@RequestParam Map<String, Object> data,
                                   RedirectAttributes redirect) {
        businessSettingService.storeRecaptcha(data);
        redirect.addFlashAttribute("success", "reCAPTCHA configuration updated.");
        return "redirect:/admin/business/configuration/third-party/recaptcha";
    }

    // ── Google Map API ──────────────────────────────────────────────────────

    @GetMapping("/google-map")
    public String map(Model model) {
        model.addAttribute("googleMap",
                businessSettingService.getValue("google_map_api", SettingsType.GOOGLE_MAP_API));
        return "admin/business/configuration/google-map";
    }

    @PostMapping("/google-map/update")
    public String updateMap(@RequestParam Map<String, Object> data, RedirectAttributes redirect) {
        businessSettingService.storeGoogleMapApi(data);
        redirect.addFlashAttribute("success", "Google Map API configuration updated.");
        return "redirect:/admin/business/configuration/third-party/google-map";
    }
}
