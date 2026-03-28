package com.drivedash.faremanagement.controller.web;

import com.drivedash.faremanagement.dto.TripFareSetupRequest;
import com.drivedash.faremanagement.service.TripFareService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/fare/trip")
@RequiredArgsConstructor
public class TripFareWebController {

    private final TripFareService tripFareService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("zones", tripFareService.getActiveZones());
        model.addAttribute("categories", tripFareService.getActiveCategories());
        return "admin/fare/trip/index";
    }

    @GetMapping("/{zoneId}/setup")
    public String setup(@PathVariable UUID zoneId, Model model) {
        model.addAttribute("zone", tripFareService.findZoneById(zoneId));
        model.addAttribute("categories", tripFareService.getActiveCategories());
        model.addAttribute("defaultFare", tripFareService.getDefaultFare(zoneId).orElse(null));
        model.addAttribute("categoryFares", tripFareService.getCategoryFares(zoneId));
        model.addAttribute("req", new TripFareSetupRequest());
        return "admin/fare/trip/setup";
    }

    @PostMapping("/{zoneId}/setup")
    public String store(@PathVariable UUID zoneId,
                        @Valid @ModelAttribute("req") TripFareSetupRequest req,
                        BindingResult br,
                        Model model,
                        RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("zone", tripFareService.findZoneById(zoneId));
            model.addAttribute("categories", tripFareService.getActiveCategories());
            model.addAttribute("defaultFare", tripFareService.getDefaultFare(zoneId).orElse(null));
            model.addAttribute("categoryFares", tripFareService.getCategoryFares(zoneId));
            return "admin/fare/trip/setup";
        }
        try {
            tripFareService.setup(zoneId, req);
            ra.addFlashAttribute("success", "Trip fare saved successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/fare/trip";
    }
}
