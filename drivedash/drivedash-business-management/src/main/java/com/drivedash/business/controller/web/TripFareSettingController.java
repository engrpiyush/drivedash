package com.drivedash.business.controller.web;

import com.drivedash.business.dto.CancellationReasonRequest;
import com.drivedash.business.entity.SettingsType;
import com.drivedash.business.service.BusinessSettingService;
import com.drivedash.business.service.CancellationReasonService;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles trip fare settings and cancellation reason CRUD.
 * Routes: /admin/business/setup/trip-fare/**
 */
@Controller
@RequestMapping("/admin/business/setup/trip-fare")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class TripFareSettingController {

    private final BusinessSettingService businessSettingService;
    private final CancellationReasonService cancellationReasonService;

    @GetMapping("/penalty")
    public String penaltyIndex(Model model) {
        model.addAttribute("cancellationReasons", cancellationReasonService.findAll());
        model.addAttribute("newReason", new CancellationReasonRequest());
        model.addAttribute("tripSettings",
                businessSettingService.findAllByType(SettingsType.TRIP_SETTINGS));
        return "admin/business/setup/fare_and_penalty";
    }

    @GetMapping("/trips")
    public String tripIndex(Model model) {
        model.addAttribute("tripFareSettings",
                businessSettingService.findAllByType(SettingsType.TRIP_FARE_SETTINGS));
        model.addAttribute("cancellationReasons", cancellationReasonService.findAll());
        model.addAttribute("newReason", new CancellationReasonRequest());
        return "admin/business/setup/trips";
    }

    @PostMapping("/store")
    public String store(@RequestParam Map<String, Object> data, RedirectAttributes redirect) {
        businessSettingService.storeTripFareSettings(data);
        redirect.addFlashAttribute("success", "Trip fare settings updated.");
        return "redirect:/admin/business/setup/trip-fare/trips";
    }

    // ── Cancellation reasons ────────────────────────────────────────────────

    @PostMapping("/cancellation-reason/store")
    public String storeCancellationReason(
            @Valid @ModelAttribute("newReason") CancellationReasonRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirect,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("cancellationReasons", cancellationReasonService.findAll());
            return "admin/business/setup/fare_and_penalty";
        }
        cancellationReasonService.create(request);
        redirect.addFlashAttribute("success", "Cancellation reason added.");
        return "redirect:/admin/business/setup/trip-fare/penalty";
    }

    @GetMapping("/cancellation-reason/edit/{id}")
    public String editCancellationReason(@PathVariable UUID id, Model model) {
        model.addAttribute("reason", cancellationReasonService.findById(id));
        return "admin/business/setup/edit-cancellation-reason";
    }

    @PostMapping("/cancellation-reason/update/{id}")
    public String updateCancellationReason(
            @PathVariable UUID id,
            @Valid @ModelAttribute CancellationReasonRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirect) {

        if (bindingResult.hasErrors()) {
            return "admin/business/setup/edit-cancellation-reason";
        }
        cancellationReasonService.update(id, request);
        redirect.addFlashAttribute("success", "Cancellation reason updated.");
        return "redirect:/admin/business/setup/trip-fare/penalty";
    }

    @DeleteMapping("/cancellation-reason/delete/{id}")
    public String deleteCancellationReason(@PathVariable UUID id, RedirectAttributes redirect) {
        cancellationReasonService.delete(id);
        redirect.addFlashAttribute("success", "Cancellation reason deleted.");
        return "redirect:/admin/business/setup/trip-fare/penalty";
    }

    @GetMapping("/cancellation-reason/status")
    public String statusCancellationReason(@RequestParam UUID id,
                                            @RequestParam boolean status,
                                            RedirectAttributes redirect) {
        cancellationReasonService.updateStatus(id, status);
        redirect.addFlashAttribute("success", "Status updated.");
        return "redirect:/admin/business/setup/trip-fare/penalty";
    }
}
