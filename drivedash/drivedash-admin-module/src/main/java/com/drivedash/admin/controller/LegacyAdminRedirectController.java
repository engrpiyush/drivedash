package com.drivedash.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Backward-compatible redirects for legacy admin URLs that still appear in
 * bookmarks or cached links.
 */
@Controller
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','EMPLOYEE')")
public class LegacyAdminRedirectController {

    @GetMapping("/admin/users")
    public String users() {
        return "redirect:/admin/customers";
    }

    @GetMapping("/admin/profile")
    public String profile() {
        return "redirect:/admin/settings";
    }

    @GetMapping("/admin/parcels")
    public String parcels() {
        return "redirect:/admin/parcel/categories";
    }

    @GetMapping("/admin/fares")
    public String fares() {
        return "redirect:/admin/fare/trip";
    }

    @GetMapping("/admin/promotions")
    public String promotions() {
        return "redirect:/admin/promotion/coupons";
    }

    @GetMapping("/admin/chats")
    public String chats() {
        return "redirect:/admin/chatting";
    }

    @GetMapping("/admin/gateways")
    public String gateways() {
        return "redirect:/admin/business/configuration/payment-gateways";
    }
}
