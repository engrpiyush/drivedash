package com.drivemond.vehicle.controller.web;

import com.drivemond.core.exception.DrivemondException;
import com.drivemond.vehicle.dto.VehicleCategoryRequest;
import com.drivemond.vehicle.entity.VehicleCategory;
import com.drivemond.vehicle.service.VehicleCategoryService;
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
@RequestMapping("/admin/vehicle/attribute-setup/category")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class VehicleCategoryWebController {

    private static final int PAGE_SIZE = 10;
    private final VehicleCategoryService categoryService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "all") String status,
                        @RequestParam(required = false) String search,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<VehicleCategory> categories = categoryService.getPage(search, status,
                PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending()));
        model.addAttribute("categories", categories);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("categoryRequest", new VehicleCategoryRequest());
        return "admin/vehicle/category/index";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute VehicleCategoryRequest request,
                        BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicle/attribute-setup/category";
        }
        try {
            categoryService.create(request);
            ra.addFlashAttribute("success", "Category created successfully");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/category";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "admin/vehicle/category/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute VehicleCategoryRequest request,
                         BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicle/attribute-setup/category/" + id + "/edit";
        }
        try {
            categoryService.update(id, request);
            ra.addFlashAttribute("success", "Category updated successfully");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/category";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("success", "Category deleted");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/category";
    }

    @GetMapping("/status")
    public String status(@RequestParam UUID id, @RequestParam boolean status,
                         @RequestParam(defaultValue = "all") String currentStatus,
                         RedirectAttributes ra) {
        try { categoryService.toggleStatus(id, status); } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/category?status=" + currentStatus;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/trashed")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("categories", categoryService.getTrashed(
                PageRequest.of(page, PAGE_SIZE)));
        return "admin/vehicle/category/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/restore")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        categoryService.restore(id);
        ra.addFlashAttribute("success", "Category restored");
        return "redirect:/admin/vehicle/attribute-setup/category/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}/permanent")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        categoryService.permanentDelete(id);
        ra.addFlashAttribute("success", "Category permanently deleted");
        return "redirect:/admin/vehicle/attribute-setup/category/trashed";
    }

    /** AJAX for vehicle create/edit category dropdown */
    @GetMapping("/ajax")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> ajaxList() {
        return ResponseEntity.ok(
                categoryService.getActiveList().stream()
                        .map(c -> Map.<String, Object>of("id", c.getId(), "text", c.getName()))
                        .toList());
    }
}
