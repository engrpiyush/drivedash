package com.drivedash.admin.controller;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/update")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class UpdateController {

    private final Flyway flyway;

    @GetMapping
    public String index(Model model) {
        var info = flyway.info();
        model.addAttribute("currentVersion",
                info.current() != null ? info.current().getVersion().getVersion() : "N/A");
        model.addAttribute("pendingCount",
                info.pending().length);
        return "update/index";
    }

    @PostMapping("/apply")
    public String apply(RedirectAttributes ra) {
        try {
            var result = flyway.migrate();
            ra.addFlashAttribute("success",
                    "Migrations applied: " + result.migrationsExecuted + " script(s).");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Migration failed: " + e.getMessage());
        }
        return "redirect:/admin/update";
    }
}
