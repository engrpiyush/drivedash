package com.drivemond.trip.controller.web;

import com.drivemond.trip.service.TripRequestService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/trips")
@RequiredArgsConstructor
public class TripWebController {

    private final TripRequestService tripService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("trips", tripService.getPage(type, status, search, page, 15));
        model.addAttribute("counts", tripService.getStatusCounts());
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        return "admin/trip/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable UUID id, Model model) {
        var trip = tripService.findById(id);
        model.addAttribute("trip", trip);
        model.addAttribute("tripStatus", tripService.getStatus(id).orElse(null));
        model.addAttribute("fee", tripService.getFee(id).orElse(null));
        model.addAttribute("time", tripService.getTime(id).orElse(null));
        model.addAttribute("coordinate", tripService.getCoordinate(id).orElse(null));
        return "admin/trip/show";
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(Model model) {
        model.addAttribute("trips", tripService.getTrashed());
        return "admin/trip/trashed";
    }

    @GetMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        tripService.restore(id);
        ra.addFlashAttribute("success", "Trip restored");
        return "redirect:/admin/trips/trashed";
    }

    @PostMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        tripService.permanentDelete(id);
        ra.addFlashAttribute("success", "Trip permanently deleted");
        return "redirect:/admin/trips/trashed";
    }
}
