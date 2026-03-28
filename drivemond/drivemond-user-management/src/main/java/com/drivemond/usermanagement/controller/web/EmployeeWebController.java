package com.drivemond.usermanagement.controller.web;

import com.drivemond.auth.entity.User;
import com.drivemond.usermanagement.dto.EmployeeRequest;
import com.drivemond.usermanagement.service.EmployeeService;
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
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService employeeService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<User> employees = employeeService.getPage(search, status, page, 15);
        model.addAttribute("employees", employees);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "admin/employee/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        populateFormModel(model);
        model.addAttribute("req", new EmployeeRequest());
        return "admin/employee/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") EmployeeRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) { populateFormModel(model); return "admin/employee/create"; }
        try {
            employeeService.create(req);
            ra.addFlashAttribute("success", "Employee created successfully");
            return "redirect:/admin/employees";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            populateFormModel(model);
            return "admin/employee/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        User employee = employeeService.findById(id);
        EmployeeRequest req = new EmployeeRequest();
        req.setFirstName(employee.getFirstName());
        req.setLastName(employee.getLastName());
        req.setEmail(employee.getEmail());
        req.setPhone(employee.getPhone());
        req.setActive(employee.isActive());
        employee.getRoles().stream().findFirst().ifPresent(r -> req.setRoleId(r.getId()));
        model.addAttribute("employee", employee);
        model.addAttribute("req", req);
        populateFormModel(model);
        return "admin/employee/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") EmployeeRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("employee", employeeService.findById(id));
            populateFormModel(model);
            return "admin/employee/edit";
        }
        try {
            employeeService.update(id, req);
            ra.addFlashAttribute("success", "Employee updated successfully");
            return "redirect:/admin/employees";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("employee", employeeService.findById(id));
            populateFormModel(model);
            return "admin/employee/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        employeeService.delete(id);
        ra.addFlashAttribute("success", "Employee deleted");
        return "redirect:/admin/employees";
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                                @RequestParam boolean status,
                                @RequestParam(defaultValue = "all") String currentStatus) {
        employeeService.toggleStatus(id, status);
        return "redirect:/admin/employees?status=" + currentStatus;
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("employees", employeeService.getPage("", "all", page, 15));
        return "admin/employee/trashed";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void populateFormModel(Model model) {
        model.addAttribute("roles", employeeService.getActiveRoles());
    }
}
