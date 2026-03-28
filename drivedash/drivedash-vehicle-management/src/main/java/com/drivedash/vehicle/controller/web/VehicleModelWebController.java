package com.drivedash.vehicle.controller.web;

import com.drivedash.core.exception.DrivedashException;
import com.drivedash.vehicle.dto.VehicleModelRequest;
import com.drivedash.vehicle.entity.VehicleBrand;
import com.drivedash.vehicle.entity.VehicleModel;
import com.drivedash.vehicle.service.VehicleBrandService;
import com.drivedash.vehicle.service.VehicleModelService;
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
@RequestMapping("/admin/vehicle/attribute-setup/model")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class VehicleModelWebController {

    private static final int PAGE_SIZE = 10;
    private final VehicleModelService modelService;
    private final VehicleBrandService brandService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "all") String status,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) UUID brandId,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<VehicleModel> models = modelService.getPage(search, brandId, status,
                PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending()));
        List<VehicleBrand> brands = brandService.getActiveList();
        model.addAttribute("models", models);
        model.addAttribute("brands", brands);
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        model.addAttribute("selectedBrandId", brandId);
        model.addAttribute("modelRequest", new VehicleModelRequest());
        return "admin/vehicle/model/index";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute VehicleModelRequest request,
                        BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicle/attribute-setup/model";
        }
        try {
            modelService.create(request);
            ra.addFlashAttribute("success", "Model created successfully");
        } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/model";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        VehicleModel vehicleModel = modelService.findById(id);
        model.addAttribute("vehicleModel", vehicleModel);
        model.addAttribute("brands", brandService.getActiveList());
        return "admin/vehicle/model/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute VehicleModelRequest request,
                         BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicle/attribute-setup/model/" + id + "/edit";
        }
        try {
            modelService.update(id, request);
            ra.addFlashAttribute("success", "Model updated successfully");
        } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/model";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            modelService.delete(id);
            ra.addFlashAttribute("success", "Model deleted");
        } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/model";
    }

    @GetMapping("/status")
    public String status(@RequestParam UUID id, @RequestParam boolean status,
                         @RequestParam(defaultValue = "all") String currentStatus,
                         RedirectAttributes ra) {
        try { modelService.toggleStatus(id, status); } catch (DrivedashException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicle/attribute-setup/model?status=" + currentStatus;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/trashed")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("models", modelService.getTrashed(PageRequest.of(page, PAGE_SIZE)));
        return "admin/vehicle/model/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/restore")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        modelService.restore(id);
        ra.addFlashAttribute("success", "Model restored");
        return "redirect:/admin/vehicle/attribute-setup/model/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}/permanent")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        modelService.permanentDelete(id);
        ra.addFlashAttribute("success", "Model permanently deleted");
        return "redirect:/admin/vehicle/attribute-setup/model/trashed";
    }

    /** AJAX: models by brand for vehicle create/edit cascading dropdown */
    @GetMapping("/ajax/{brandId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> ajaxByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(
                modelService.getActiveByBrand(brandId).stream()
                        .map(m -> Map.<String, Object>of("id", m.getId(), "text", m.getName()))
                        .toList());
    }
}
