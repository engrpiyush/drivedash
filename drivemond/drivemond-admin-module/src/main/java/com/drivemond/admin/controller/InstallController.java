package com.drivemond.admin.controller;

import com.drivemond.admin.service.InstallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/install")
@RequiredArgsConstructor
public class InstallController {

    private final InstallService installService;

    @GetMapping({"", "/"})
    public String start() {
        if (installService.isInstalled()) return "redirect:/auth/login";
        return "redirect:/install/step/0";
    }

    @GetMapping("/step/0")
    public String step0() {
        if (installService.isInstalled()) return "redirect:/auth/login";
        return "install/step0";
    }

    @GetMapping("/step/1")
    public String step1(Model model) {
        if (installService.isInstalled()) return "redirect:/auth/login";
        model.addAttribute("javaVersion", installService.getJavaVersion());
        model.addAttribute("memoryMb", installService.getAvailableMemoryMb());
        model.addAttribute("dbOk", installService.testDatabaseConnection());
        return "install/step1";
    }

    @GetMapping("/step/2")
    public String step2() {
        if (installService.isInstalled()) return "redirect:/auth/login";
        return "install/step2";
    }

    @PostMapping("/step/2")
    public String createAdmin(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam String phone,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               RedirectAttributes ra) {
        if (installService.isInstalled()) return "redirect:/auth/login";
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/install/step/2";
        }
        if (password.length() < 8) {
            ra.addFlashAttribute("error", "Password must be at least 8 characters.");
            return "redirect:/install/step/2";
        }
        try {
            installService.createSuperAdmin(firstName, lastName, email, phone, password);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Setup failed: " + e.getMessage());
            return "redirect:/install/step/2";
        }
        return "redirect:/install/complete";
    }

    @GetMapping("/complete")
    public String complete() {
        return "install/complete";
    }
}
