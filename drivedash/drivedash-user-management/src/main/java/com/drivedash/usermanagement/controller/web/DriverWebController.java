package com.drivedash.usermanagement.controller.web;

import com.drivedash.auth.entity.User;
import com.drivedash.usermanagement.dto.DriverRequest;
import com.drivedash.usermanagement.service.DriverService;
import com.drivedash.usermanagement.service.UserLevelService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/drivers")
@RequiredArgsConstructor
public class DriverWebController {

    private final DriverService driverService;
    private final UserLevelService levelService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<User> drivers = driverService.getPage(search, status, page, 15);
        model.addAttribute("drivers", drivers);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "admin/driver/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        populateFormModel(model);
        model.addAttribute("req", new DriverRequest());
        return "admin/driver/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") DriverRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { populateFormModel(model); return "admin/driver/create"; }
        try {
            driverService.create(req);
            ra.addFlashAttribute("success", "Driver created successfully");
            return "redirect:/admin/drivers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            populateFormModel(model);
            return "admin/driver/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        User driver = driverService.findById(id);
        DriverRequest req = new DriverRequest();
        req.setFirstName(driver.getFirstName());
        req.setLastName(driver.getLastName());
        req.setEmail(driver.getEmail());
        req.setPhone(driver.getPhone());
        req.setUserLevelId(driver.getUserLevelId());
        req.setIdentificationNumber(driver.getIdentificationNumber());
        req.setIdentificationType(driver.getIdentificationType());
        req.setActive(driver.isActive());
        model.addAttribute("driver", driver);
        model.addAttribute("req", req);
        populateFormModel(model);
        return "admin/driver/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") DriverRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("driver", driverService.findById(id));
            populateFormModel(model);
            return "admin/driver/edit";
        }
        try {
            driverService.update(id, req);
            ra.addFlashAttribute("success", "Driver updated successfully");
            return "redirect:/admin/drivers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("driver", driverService.findById(id));
            populateFormModel(model);
            return "admin/driver/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        driverService.delete(id);
        ra.addFlashAttribute("success", "Driver deleted");
        return "redirect:/admin/drivers";
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                                @RequestParam boolean status,
                                @RequestParam(defaultValue = "all") String currentStatus) {
        driverService.toggleStatus(id, status);
        return "redirect:/admin/drivers?status=" + currentStatus;
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable UUID id, RedirectAttributes ra) {
        driverService.approve(id);
        ra.addFlashAttribute("success", "Driver approved");
        return "redirect:/admin/drivers";
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("drivers", driverService.getPage("", "all", page, 15));
        return "admin/driver/trashed";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void populateFormModel(Model model) {
        model.addAttribute("levels", levelService.getActiveByType("driver"));
    }
}
