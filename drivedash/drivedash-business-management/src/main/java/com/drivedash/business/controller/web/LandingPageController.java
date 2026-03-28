package com.drivedash.business.controller.web;

import com.drivedash.business.entity.SettingsType;
import com.drivedash.business.service.BusinessSettingService;
import com.drivedash.business.service.SocialLinkService;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Public-facing landing pages.
 * Mirrors Laravel's {@code LandingPageController}.
 *
 * <p>Routes: /, /about-us, /contact-us, /privacy, /terms
 */
@Controller
@RequiredArgsConstructor
public class LandingPageController {

    private final BusinessSettingService businessSettingService;
    private final SocialLinkService socialLinkService;
    private final EntityManager em;

    @GetMapping("/")
    @Transactional(readOnly = true)
    public String index(Model model) {
        model.addAttribute("introSection",
                businessSettingService.getValue("intro_section", SettingsType.LANDING_PAGES_SETTINGS));
        model.addAttribute("ourSolutions",
                businessSettingService.getValue("our_solutions", SettingsType.LANDING_PAGES_SETTINGS));
        model.addAttribute("businessStats",
                businessSettingService.getValue("business_statistics", SettingsType.LANDING_PAGES_SETTINGS));
        model.addAttribute("earnMoney",
                businessSettingService.getValue("earn_money", SettingsType.LANDING_PAGES_SETTINGS));
        model.addAttribute("testimonialSection",
                businessSettingService.getValue("testimonial", SettingsType.LANDING_PAGES_SETTINGS));
        model.addAttribute("callToAction",
                businessSettingService.getValue("call_to_action", SettingsType.LANDING_PAGES_SETTINGS));
        model.addAttribute("businessName",
                businessSettingService.getScalar("business_name", SettingsType.BUSINESS_INFORMATION));
        model.addAttribute("businessLogo",
                businessSettingService.getScalar("header_logo", SettingsType.BUSINESS_INFORMATION));
        model.addAttribute("socialLinks", socialLinkService.findAllActive());
        model.addAttribute("testimonials", getSavedTestimonials());
        return "landing/index";
    }

    @GetMapping("/about-us")
    public String aboutUs(Model model) {
        populateCommon(model);
        model.addAttribute("pageContent",
                businessSettingService.getValue("about_us", SettingsType.PAGES_SETTINGS));
        return "landing/about";
    }

    @GetMapping("/contact-us")
    public String contactUs(Model model) {
        populateCommon(model);
        model.addAttribute("businessInfo",
                businessSettingService.findAllByType(SettingsType.BUSINESS_INFORMATION));
        return "landing/contact";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        populateCommon(model);
        model.addAttribute("pageContent",
                businessSettingService.getValue("privacy_policy", SettingsType.PAGES_SETTINGS));
        return "landing/privacy";
    }

    @GetMapping("/terms")
    public String terms(Model model) {
        populateCommon(model);
        model.addAttribute("pageContent",
                businessSettingService.getValue("terms_and_conditions", SettingsType.PAGES_SETTINGS));
        return "landing/terms";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void populateCommon(Model model) {
        model.addAttribute("businessName",
                businessSettingService.getScalar("business_name", SettingsType.BUSINESS_INFORMATION));
        model.addAttribute("businessLogo",
                businessSettingService.getScalar("header_logo", SettingsType.BUSINESS_INFORMATION));
        model.addAttribute("socialLinks", socialLinkService.findAllActive());
    }

    /** Loads reviews marked as saved (is_saved = true) for the testimonials section. */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getSavedTestimonials() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Object[]> rows = em.createNativeQuery(
                    "SELECT r.rating, r.feedback, u.first_name, u.last_name, u.profile_image " +
                    "FROM reviews r " +
                    "LEFT JOIN users u ON u.id = r.received_by " +
                    "WHERE r.is_saved = 1 AND r.deleted_at IS NULL " +
                    "ORDER BY r.created_at DESC LIMIT 10").getResultList();
            for (Object[] row : rows) {
                Map<String, Object> t = new LinkedHashMap<>();
                t.put("rating",        row[0]);
                t.put("feedback",      row[1]);
                t.put("name",          row[2] + " " + row[3]);
                t.put("profileImage",  row[4]);
                result.add(t);
            }
        } catch (Exception ignored) {}
        return result;
    }
}
