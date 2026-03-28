package com.drivedash.business.controller.web;

import com.drivedash.business.dto.SocialLinkRequest;
import com.drivedash.business.service.BusinessSettingService;
import com.drivedash.business.service.SocialLinkService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pages & Media management: social links and business pages (T&C, Privacy Policy, etc.).
 * Routes: /admin/business/pages-media/**
 */
@Controller
@RequestMapping("/admin/business/pages-media")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class PagesMediaController {

    private final SocialLinkService socialLinkService;
    private final BusinessSettingService businessSettingService;

    // ── Social links ────────────────────────────────────────────────────────

    @GetMapping("/social-media")
    public String socialMedia(Model model) {
        model.addAttribute("socialLinks", socialLinkService.findAll());
        model.addAttribute("newLink", new SocialLinkRequest());
        return "admin/business/pages/social-media";
    }

    @PostMapping("/store-social-link")
    public String storeSocialLink(@Valid SocialLinkRequest request,
                                   RedirectAttributes redirect) {
        socialLinkService.create(request.getName(), request.getLink());
        redirect.addFlashAttribute("success", "Social link added.");
        return "redirect:/admin/business/pages-media/social-media";
    }

    @PostMapping("/update-social-link/{id}")
    public String updateSocialLink(@PathVariable UUID id,
                                    @Valid SocialLinkRequest request,
                                    RedirectAttributes redirect) {
        socialLinkService.update(id, request.getName(), request.getLink());
        redirect.addFlashAttribute("success", "Social link updated.");
        return "redirect:/admin/business/pages-media/social-media";
    }

    @GetMapping("/update-social-link-status")
    public String updateSocialStatus(@RequestParam UUID id,
                                      @RequestParam boolean status,
                                      RedirectAttributes redirect) {
        socialLinkService.updateStatus(id, status);
        redirect.addFlashAttribute("success", "Status updated.");
        return "redirect:/admin/business/pages-media/social-media";
    }

    @DeleteMapping("/delete-social-link")
    public String deleteSocialLink(@RequestParam UUID id, RedirectAttributes redirect) {
        socialLinkService.delete(id);
        redirect.addFlashAttribute("success", "Social link deleted.");
        return "redirect:/admin/business/pages-media/social-media";
    }

    // ── Business pages (T&C, Privacy, etc.) ────────────────────────────────

    @GetMapping("/business-page")
    public String businessPages(Model model) {
        return "admin/business/pages/business-pages";
    }

    @PostMapping("/business-page/update")
    public String businessPagesUpdate(
            @RequestParam String type,
            @RequestParam String shortDescription,
            @RequestParam String longDescription,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirect) {

        businessSettingService.storeBusinessPage(type, shortDescription, longDescription, image);
        redirect.addFlashAttribute("success", "Page updated successfully.");
        return "redirect:/admin/business/pages-media/business-page";
    }
}
