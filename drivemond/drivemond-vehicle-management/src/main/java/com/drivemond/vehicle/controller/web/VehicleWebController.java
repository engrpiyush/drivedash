package com.drivemond.vehicle.controller.web;

import com.drivemond.auth.entity.UserType;
import com.drivemond.auth.repository.UserRepository;
import com.drivemond.core.exception.DrivemondException;
import com.drivemond.vehicle.dto.VehicleRequest;
import com.drivemond.vehicle.entity.Vehicle;
import com.drivemond.vehicle.enums.FuelType;
import com.drivemond.vehicle.enums.Ownership;
import com.drivemond.vehicle.service.VehicleBrandService;
import com.drivemond.vehicle.service.VehicleCategoryService;
import com.drivemond.vehicle.service.VehicleService;
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
@RequestMapping("/admin/vehicles")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','EMPLOYEE')")
@RequiredArgsConstructor
public class VehicleWebController {

    private static final int PAGE_SIZE = 10;

    private final VehicleService vehicleService;
    private final VehicleCategoryService categoryService;
    private final VehicleBrandService brandService;
    private final UserRepository userRepository;

    @GetMapping
    public String index(@RequestParam(defaultValue = "all") String status,
                        @RequestParam(required = false) String search,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        Page<Vehicle> vehicles = vehicleService.getPage(search, status,
                PageRequest.of(page, PAGE_SIZE, Sort.by("updatedAt").descending()));
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("categories", categoryService.getActiveList());
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        return "admin/vehicle/vehicle/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public String create(Model model) {
        model.addAttribute("vehicleRequest", new VehicleRequest());
        model.addAttribute("brands", brandService.getActiveList());
        model.addAttribute("categories", categoryService.getActiveList());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("ownerships", Ownership.values());
        return "admin/vehicle/vehicle/create";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public String store(@Valid @ModelAttribute VehicleRequest request,
                        BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicles/create";
        }
        try {
            vehicleService.create(request);
            ra.addFlashAttribute("success", "Vehicle created successfully");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public String edit(@PathVariable UUID id, Model model) {
        Vehicle vehicle = vehicleService.findById(id);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("brands", brandService.getActiveList());
        model.addAttribute("categories", categoryService.getActiveList());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("ownerships", Ownership.values());
        return "admin/vehicle/vehicle/edit";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute VehicleRequest request,
                         BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", br.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/vehicles/" + id + "/edit";
        }
        try {
            vehicleService.update(id, request);
            ra.addFlashAttribute("success", "Vehicle updated successfully");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            vehicleService.delete(id);
            ra.addFlashAttribute("success", "Vehicle deleted");
        } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public String toggleStatus(@RequestParam UUID id, @RequestParam boolean status,
                               @RequestParam(defaultValue = "all") String currentStatus,
                               RedirectAttributes ra) {
        try { vehicleService.toggleStatus(id, status); } catch (DrivemondException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/vehicles?status=" + currentStatus;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/trashed")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("vehicles", vehicleService.getTrashed(PageRequest.of(page, PAGE_SIZE)));
        return "admin/vehicle/vehicle/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/restore")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        vehicleService.restore(id);
        ra.addFlashAttribute("success", "Vehicle restored");
        return "redirect:/admin/vehicles/trashed";
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}/permanent")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        vehicleService.permanentDelete(id);
        ra.addFlashAttribute("success", "Vehicle permanently deleted");
        return "redirect:/admin/vehicles/trashed";
    }

    /**
     * AJAX: return active drivers for the vehicle create/edit driver dropdown.
     * Queries users with userType = DRIVER.
     */
    @GetMapping("/ajax/drivers")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> ajaxDrivers(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .filter(u -> u.getUserType() == UserType.DRIVER)
                        .filter(u -> search == null || u.getFirstName() != null &&
                                (u.getFirstName() + " " + u.getLastName()).toLowerCase()
                                        .contains(search.toLowerCase()))
                        .map(u -> Map.<String, Object>of(
                                "id", u.getId(),
                                "text", (u.getFirstName() != null ? u.getFirstName() : "") +
                                        " " + (u.getLastName() != null ? u.getLastName() : "") +
                                        (u.getPhone() != null ? " (" + u.getPhone() + ")" : "")))
                        .toList());
    }
}
