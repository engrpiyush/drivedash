package com.drivemond.promotion.controller.web;

import com.drivemond.promotion.dto.BannerSetupRequest;
import com.drivemond.promotion.service.BannerSetupService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/promotion/banners")
@RequiredArgsConstructor
public class BannerSetupWebController {

    private final BannerSetupService bannerService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("banners", bannerService.getPage(search, status, page, 15));
        model.addAttribute("req", new BannerSetupRequest());
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        return "admin/promotion/banner/index";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") BannerSetupRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("banners", bannerService.getPage("", "all", 0, 15));
            model.addAttribute("status", "all");
            model.addAttribute("search", "");
            return "admin/promotion/banner/index";
        }
        try {
            bannerService.create(req);
            ra.addFlashAttribute("success", "Banner created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promotion/banners";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("banner", bannerService.findById(id));
        model.addAttribute("req", new BannerSetupRequest());
        return "admin/promotion/banner/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") BannerSetupRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("banner", bannerService.findById(id));
            return "admin/promotion/banner/edit";
        }
        try {
            bannerService.update(id, req);
            ra.addFlashAttribute("success", "Banner updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promotion/banners";
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                               @RequestParam boolean active,
                               @RequestParam(defaultValue = "all") String status) {
        bannerService.toggleStatus(id, active);
        return "redirect:/admin/promotion/banners?status=" + status;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        bannerService.delete(id);
        ra.addFlashAttribute("success", "Banner deleted");
        return "redirect:/admin/promotion/banners";
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(Model model) {
        model.addAttribute("banners", bannerService.getTrashed());
        return "admin/promotion/banner/trashed";
    }

    @GetMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        bannerService.restore(id);
        ra.addFlashAttribute("success", "Banner restored");
        return "redirect:/admin/promotion/banners/trashed";
    }

    @PostMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        bannerService.permanentDelete(id);
        ra.addFlashAttribute("success", "Banner permanently deleted");
        return "redirect:/admin/promotion/banners/trashed";
    }
}
