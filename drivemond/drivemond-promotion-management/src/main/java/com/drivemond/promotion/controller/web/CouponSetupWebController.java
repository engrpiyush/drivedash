package com.drivemond.promotion.controller.web;

import com.drivemond.promotion.dto.CouponSetupRequest;
import com.drivemond.promotion.service.CouponSetupService;
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
@RequestMapping("/admin/promotion/coupons")
@RequiredArgsConstructor
public class CouponSetupWebController {

    private final CouponSetupService couponService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("coupons", couponService.getPage(search, status, page, 15));
        model.addAttribute("cards", couponService.getCardValues());
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        return "admin/promotion/coupon/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("req", new CouponSetupRequest());
        model.addAttribute("userLevels", couponService.getUserLevels());
        return "admin/promotion/coupon/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") CouponSetupRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("userLevels", couponService.getUserLevels());
            return "admin/promotion/coupon/create";
        }
        try {
            couponService.create(req);
            ra.addFlashAttribute("success", "Coupon created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promotion/coupons";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("coupon", couponService.findById(id));
        model.addAttribute("req", new CouponSetupRequest());
        model.addAttribute("userLevels", couponService.getUserLevels());
        return "admin/promotion/coupon/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") CouponSetupRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("coupon", couponService.findById(id));
            model.addAttribute("userLevels", couponService.getUserLevels());
            return "admin/promotion/coupon/edit";
        }
        try {
            couponService.update(id, req);
            ra.addFlashAttribute("success", "Coupon updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promotion/coupons";
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                               @RequestParam boolean active,
                               @RequestParam(defaultValue = "all") String status) {
        couponService.toggleStatus(id, active);
        return "redirect:/admin/promotion/coupons?status=" + status;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        couponService.delete(id);
        ra.addFlashAttribute("success", "Coupon deleted");
        return "redirect:/admin/promotion/coupons";
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(Model model) {
        model.addAttribute("coupons", couponService.getTrashed());
        return "admin/promotion/coupon/trashed";
    }

    @GetMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        couponService.restore(id);
        ra.addFlashAttribute("success", "Coupon restored");
        return "redirect:/admin/promotion/coupons/trashed";
    }

    @PostMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        couponService.permanentDelete(id);
        ra.addFlashAttribute("success", "Coupon permanently deleted");
        return "redirect:/admin/promotion/coupons/trashed";
    }
}
