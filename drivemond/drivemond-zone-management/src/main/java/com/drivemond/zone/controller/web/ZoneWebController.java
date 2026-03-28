package com.drivemond.zone.controller.web;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.zone.dto.ZoneCoordinatePoint;
import com.drivemond.zone.dto.ZoneRequest;
import com.drivemond.zone.entity.Zone;
import com.drivemond.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/admin/zones")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class ZoneWebController {

    private static final int PAGE_SIZE = 10;

    private final ZoneService zoneService;

    @Value("${app.google.map-api-key:}")
    private String mapApiKey;

    // ── Index + Create form ──────────────────────────────────────────────────

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Page<Zone> zones = zoneService.getZonesPage(
                search, status, PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending()));

        model.addAttribute("zones", zones);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("mapApiKey", mapApiKey);
        model.addAttribute("zoneRequest", new ZoneRequest());
        return "admin/zone/index";
    }

    // ── Store ────────────────────────────────────────────────────────────────

    @PostMapping
    public String store(@Valid @ModelAttribute("zoneRequest") ZoneRequest request,
                        BindingResult bindingResult,
                        RedirectAttributes ra,
                        Model model) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Validation failed: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/zones";
        }
        try {
            zoneService.create(request);
            ra.addFlashAttribute("success", "Zone created successfully");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/zones";
    }

    // ── Edit form ────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        Zone zone = zoneService.findById(id);
        List<ZoneCoordinatePoint> coords = zoneService.toCoordinatePoints(zone.getCoordinates());
        ZoneCoordinatePoint centroid = zoneService.getCentroid(zone.getCoordinates());

        model.addAttribute("zone", zone);
        model.addAttribute("coordsList", coords); // Thymeleaf JS inlining serializes this automatically
        model.addAttribute("centerLat", centroid.getLat());
        model.addAttribute("centerLng", centroid.getLng());
        model.addAttribute("mapApiKey", mapApiKey);
        return "admin/zone/edit";
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute ZoneRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/zones/" + id + "/edit";
        }
        try {
            zoneService.update(id, request);
            ra.addFlashAttribute("success", "Zone updated successfully");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/zones";
    }

    // ── Delete (soft) ─────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            zoneService.delete(id);
            ra.addFlashAttribute("success", "Zone deleted successfully");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/zones";
    }

    // ── Toggle status ────────────────────────────────────────────────────────

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                               @RequestParam boolean status,
                               @RequestParam(defaultValue = "all") String currentStatus,
                               RedirectAttributes ra) {
        try {
            zoneService.toggleStatus(id, status);
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/zones?status=" + currentStatus;
    }

    // ── Trashed ──────────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/trashed")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Zone> zones = zoneService.getTrashedZones(
                PageRequest.of(page, PAGE_SIZE, Sort.by("deletedAt").descending()));
        model.addAttribute("zones", zones);
        return "admin/zone/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/restore")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            zoneService.restore(id);
            ra.addFlashAttribute("success", "Zone restored successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to restore zone");
        }
        return "redirect:/admin/zones/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}/permanent")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            zoneService.permanentDelete(id);
            ra.addFlashAttribute("success", "Zone permanently deleted");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to permanently delete zone");
        }
        return "redirect:/admin/zones/trashed";
    }

    // ── AJAX endpoints for Google Maps ────────────────────────────────────────

    /**
     * Returns all zone polygons as [[{lat,lng},...], ...] for map rendering.
     */
    @GetMapping("/get-zones")
    @ResponseBody
    public List<List<Map<String, Double>>> getZonesJson(
            @RequestParam(defaultValue = "all") String status) {
        List<Zone> zones = zoneService.getZonesForMap(status);
        List<List<Map<String, Double>>> result = new ArrayList<>();
        for (Zone zone : zones) {
            if (zone.getCoordinates() != null) {
                List<ZoneCoordinatePoint> pts = zoneService.toCoordinatePoints(zone.getCoordinates());
                List<Map<String, Double>> coords = new ArrayList<>();
                for (ZoneCoordinatePoint p : pts) {
                    coords.add(Map.of("lat", p.getLat(), "lng", p.getLng()));
                }
                result.add(coords);
            }
        }
        return result;
    }

    /**
     * Returns a single zone's coordinates + centroid for the edit page map.
     */
    @GetMapping("/{id}/coordinates")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCoordinates(@PathVariable UUID id) {
        Zone zone = zoneService.findById(id);
        Map<String, Object> resp = new LinkedHashMap<>();
        List<ZoneCoordinatePoint> pts = zoneService.toCoordinatePoints(zone.getCoordinates());
        resp.put("coordinates", pts);
        ZoneCoordinatePoint centroid = zoneService.getCentroid(zone.getCoordinates());
        resp.put("centerLat", centroid.getLat());
        resp.put("centerLng", centroid.getLng());
        return ResponseEntity.ok(resp);
    }
}
