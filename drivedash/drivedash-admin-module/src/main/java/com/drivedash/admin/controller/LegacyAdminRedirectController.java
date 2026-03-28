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
}
