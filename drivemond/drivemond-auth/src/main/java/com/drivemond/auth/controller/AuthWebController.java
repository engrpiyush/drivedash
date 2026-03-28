package com.drivemond.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves Thymeleaf login/logout pages for the web admin panel.
 * Form submission is handled by Spring Security's built-in form-login filter.
 */
@Controller
@RequestMapping("/auth")
public class AuthWebController {

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email/phone or password.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out.");
        }
        if (expired != null) {
            model.addAttribute("errorMessage", "Your session has expired. Please log in again.");
        }

        return "auth/login";   // resolves to templates/auth/login.html
    }
}
