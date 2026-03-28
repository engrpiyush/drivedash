package com.drivemond.faremanagement.controller.web;

import com.drivemond.faremanagement.dto.ParcelFareSetupRequest;
import com.drivemond.faremanagement.service.ParcelFareService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/fare/parcel")
@RequiredArgsConstructor
public class ParcelFareWebController {

    private final ParcelFareService parcelFareService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("zones", parcelFareService.getActiveZones());
        return "admin/fare/parcel/index";
    }

    @GetMapping("/{zoneId}/setup")
    public String setup(@PathVariable UUID zoneId, Model model) {
        model.addAttribute("zone", parcelFareService.findZoneById(zoneId));
        model.addAttribute("fare", parcelFareService.getFareByZone(zoneId).orElse(null));
        model.addAttribute("req", new ParcelFareSetupRequest());
        return "admin/fare/parcel/setup";
    }

    @PostMapping("/{zoneId}/setup")
    public String store(@PathVariable UUID zoneId,
                        @Valid @ModelAttribute("req") ParcelFareSetupRequest req,
                        BindingResult br,
                        Model model,
                        RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("zone", parcelFareService.findZoneById(zoneId));
            model.addAttribute("fare", parcelFareService.getFareByZone(zoneId).orElse(null));
            return "admin/fare/parcel/setup";
        }
        try {
            parcelFareService.setup(zoneId, req);
            ra.addFlashAttribute("success", "Parcel fare saved successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/fare/parcel";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        parcelFareService.delete(id);
        ra.addFlashAttribute("success", "Parcel fare deleted");
        return "redirect:/admin/fare/parcel";
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(Model model) {
        model.addAttribute("fares", parcelFareService.getTrashed());
        return "admin/fare/parcel/trashed";
    }

    @GetMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        parcelFareService.restore(id);
        ra.addFlashAttribute("success", "Parcel fare restored");
        return "redirect:/admin/fare/parcel/trashed";
    }

    @PostMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        parcelFareService.permanentDelete(id);
        ra.addFlashAttribute("success", "Parcel fare permanently deleted");
        return "redirect:/admin/fare/parcel/trashed";
    }
}
