package com.drivedash.vehicle.controller.web;

import com.drivedash.core.exception.DrivedashException;
import com.drivedash.vehicle.dto.VehicleBrandRequest;
import com.drivedash.vehicle.entity.VehicleBrand;
import com.drivedash.vehicle.service.VehicleBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin/vehicle/attribute-setup/brand")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class VehicleBrandWebController {

    private static final int PAGE_SIZE = 10;
    private final VehicleBrandService brandService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "all") String status,
                        @RequestParam(required = false) String search,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<VehicleBrand> brands = brandService.getPage(search, status,
                PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending()));
        model.addAttribute("brands", brands);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("brandRequest", new VehicleBrandRequest());
        return "admin/vehicle/brand/index";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute VehicleBrandRequest request,
                        BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicle/attribute-setup/brand";
        }
        try {
            brandService.create(request);
            ra.addFlashAttribute("success", "Brand created successfully");
        } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/brand";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("brand", brandService.findById(id));
        return "admin/vehicle/brand/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute VehicleBrandRequest request,
                         BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicle/attribute-setup/brand/" + id + "/edit";
        }
        try {
            brandService.update(id, request);
            ra.addFlashAttribute("success", "Brand updated successfully");
        } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/brand";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            brandService.delete(id);
            ra.addFlashAttribute("success", "Brand deleted");
        } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/brand";
    }

    @GetMapping("/status")
    public String status(@RequestParam UUID id, @RequestParam boolean status,
                         @RequestParam(defaultValue = "all") String currentStatus,
                         RedirectAttributes ra) {
        try { brandService.toggleStatus(id, status); } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/brand?status=" + currentStatus;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/trashed")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("brands", brandService.getTrashed(
                PageRequest.of(page, PAGE_SIZE, Sort.by("deletedAt").descending())));
        return "admin/vehicle/brand/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/restore")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        brandService.restore(id);
        ra.addFlashAttribute("success", "Brand restored");
        return "redirect:/admin/vehicle/attribute-setup/brand/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}/permanent")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        brandService.permanentDelete(id);
        ra.addFlashAttribute("success", "Brand permanently deleted");
        return "redirect:/admin/vehicle/attribute-setup/brand/trashed";
    }

    /** AJAX endpoint for Select2 dropdowns in vehicle create/edit form */
    @GetMapping("/ajax")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> ajaxList(
            @RequestParam(defaultValue = "active") String status) {
        return ResponseEntity.ok(
                brandService.getActiveList().stream()
                        .map(b -> Map.<String, Object>of("id", b.getId(), "text", b.getName()))
                        .toList());
    }
}
