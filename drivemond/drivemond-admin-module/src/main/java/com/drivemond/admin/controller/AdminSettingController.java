package com.drivemond.admin.controller;

import com.drivemond.auth.entity.User;
import com.drivemond.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin profile settings: view and update name, phone, and password.
 * Route: /admin/settings
 */
@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','EMPLOYEE')")
@RequiredArgsConstructor
public class AdminSettingController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = resolveUser(principal);
        model.addAttribute("user", user);
        return "admin/dashboard/profile-settings";
    }

    @PostMapping("/update")
    public String update(@AuthenticationPrincipal UserDetails principal,
                         @RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam(required = false) String phone,
                         RedirectAttributes redirect) {
        User user = resolveUser(principal);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        if (phone != null && !phone.isBlank()) {
            user.setPhone(phone);
        }
        userRepository.save(user);
        redirect.addFlashAttribute("success", "Profile updated successfully.");
        return "redirect:/admin/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails principal,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirect) {
        if (!newPassword.equals(confirmPassword)) {
            redirect.addFlashAttribute("error", "New password and confirmation do not match.");
            return "redirect:/admin/settings";
        }
        if (newPassword.length() < 8) {
            redirect.addFlashAttribute("error", "Password must be at least 8 characters.");
            return "redirect:/admin/settings";
        }

        User user = resolveUser(principal);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirect.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/admin/settings";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redirect.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/admin/settings";
    }

    private User resolveUser(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .or(() -> userRepository.findByPhone(principal.getUsername()))
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }
}
