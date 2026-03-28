package com.drivemond.business.controller.web;

import com.drivemond.business.entity.SettingsType;
import com.drivemond.business.service.BusinessSettingService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles the Business Information setup pages.
 * Mirrors Laravel's {@code BusinessInfoController} (New/Admin path).
 * Routes: GET/POST /admin/business/setup/info/**
 */
@Controller
@RequestMapping("/admin/business/setup/info")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class BusinessInfoController {

    private final BusinessSettingService businessSettingService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("settings",
                businessSettingService.findAllByType(SettingsType.BUSINESS_INFORMATION));
        return "admin/business/setup/info";
    }

    @PostMapping("/store")
    public String store(
            @RequestParam Map<String, String> formData,
            @RequestParam(name = "header_logo",  required = false) MultipartFile headerLogo,
            @RequestParam(name = "favicon",       required = false) MultipartFile favicon,
            @RequestParam(name = "preloader",     required = false) MultipartFile preloader,
            RedirectAttributes redirect) {

        Map<String, Object> data = new HashMap<>(formData);
        Map<String, MultipartFile> files = new HashMap<>();
        if (headerLogo != null && !headerLogo.isEmpty())  files.put("header_logo", headerLogo);
        if (favicon     != null && !favicon.isEmpty())    files.put("favicon",      favicon);
        if (preloader   != null && !preloader.isEmpty())  files.put("preloader",    preloader);

        businessSettingService.storeBusinessInfo(data, files);
        redirect.addFlashAttribute("success", "Business information updated successfully.");
        return "redirect:/admin/business/setup/info";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settings",
                businessSettingService.findAllByType(SettingsType.BUSINESS_SETTINGS));
        return "admin/business/setup/settings";
    }

    @PostMapping("/update-settings")
    public String updateSettings(@RequestParam Map<String, Object> data,
                                 RedirectAttributes redirect) {
        businessSettingService.updateBusinessSettings(data);
        redirect.addFlashAttribute("success", "Settings updated successfully.");
        return "redirect:/admin/business/setup/info/settings";
    }

    @GetMapping("/maintenance")
    public String toggleMaintenance(@RequestParam boolean status,
                                    RedirectAttributes redirect) {
        businessSettingService.setMaintenanceMode(status);
        redirect.addFlashAttribute("success",
                "Maintenance mode " + (status ? "enabled" : "disabled") + ".");
        return "redirect:/admin/business/setup/info";
    }
}
