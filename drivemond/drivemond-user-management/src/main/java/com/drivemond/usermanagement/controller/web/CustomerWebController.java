package com.drivemond.usermanagement.controller.web;

import com.drivemond.auth.entity.User;
import com.drivemond.usermanagement.dto.CustomerRequest;
import com.drivemond.usermanagement.entity.UserLevel;
import com.drivemond.usermanagement.service.CustomerService;
import com.drivemond.usermanagement.service.UserLevelService;
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
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class CustomerWebController {

    private final CustomerService customerService;
    private final UserLevelService levelService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<User> customers = customerService.getPage(search, status, page, 15);
        model.addAttribute("customers", customers);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "admin/customer/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        populateFormModel(model);
        model.addAttribute("req", new CustomerRequest());
        return "admin/customer/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") CustomerRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { populateFormModel(model); return "admin/customer/create"; }
        try {
            customerService.create(req);
            ra.addFlashAttribute("success", "Customer created successfully");
            return "redirect:/admin/customers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            populateFormModel(model);
            return "admin/customer/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        User customer = customerService.findById(id);
        CustomerRequest req = new CustomerRequest();
        req.setFirstName(customer.getFirstName());
        req.setLastName(customer.getLastName());
        req.setEmail(customer.getEmail());
        req.setPhone(customer.getPhone());
        req.setUserLevelId(customer.getUserLevelId());
        req.setActive(customer.isActive());
        model.addAttribute("customer", customer);
        model.addAttribute("req", req);
        populateFormModel(model);
        return "admin/customer/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") CustomerRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("customer", customerService.findById(id));
            populateFormModel(model);
            return "admin/customer/edit";
        }
        try {
            customerService.update(id, req);
            ra.addFlashAttribute("success", "Customer updated successfully");
            return "redirect:/admin/customers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", customerService.findById(id));
            populateFormModel(model);
            return "admin/customer/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        customerService.delete(id);
        ra.addFlashAttribute("success", "Customer deleted");
        return "redirect:/admin/customers";
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                                @RequestParam boolean status,
                                @RequestParam(defaultValue = "all") String currentStatus) {
        customerService.toggleStatus(id, status);
        return "redirect:/admin/customers?status=" + currentStatus;
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("customers", customerService.getTrashed(page, 15));
        return "admin/customer/trashed";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void populateFormModel(Model model) {
        model.addAttribute("levels", levelService.getActiveByType("customer"));
    }
}
