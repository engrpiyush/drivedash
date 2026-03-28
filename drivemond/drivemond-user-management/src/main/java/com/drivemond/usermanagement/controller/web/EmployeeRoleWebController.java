package com.drivemond.usermanagement.controller.web;

import com.drivemond.usermanagement.dto.RoleRequest;
import com.drivemond.usermanagement.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/employees/roles")
@RequiredArgsConstructor
public class EmployeeRoleWebController {

    private static final List<String> ALL_MODULES = List.of(
            "customer", "driver", "employee", "zone", "vehicle",
            "trip", "parcel", "transaction", "promotion", "review",
            "business-settings", "fare-management", "withdraw", "user-level"
    );

    private final EmployeeService employeeService;

    @GetMapping
    public String index(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
                        Model model) {
        model.addAttribute("roles", employeeService.getRolePage(page, 15));
        return "admin/employee/role/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("req", new RoleRequest());
        model.addAttribute("allModules", ALL_MODULES);
        return "admin/employee/role/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") RoleRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("allModules", ALL_MODULES);
            return "admin/employee/role/create";
        }
        try {
            employeeService.createRole(req);
            ra.addFlashAttribute("success", "Role created");
            return "redirect:/admin/employees/roles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("allModules", ALL_MODULES);
            return "admin/employee/role/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        com.drivemond.auth.entity.Role role = employeeService.findRoleById(id);
        RoleRequest req = new RoleRequest();
        req.setName(role.getName());
        req.setModules(role.getModules());
        req.setActive(role.isActive());
        model.addAttribute("role", role);
        model.addAttribute("req", req);
        model.addAttribute("allModules", ALL_MODULES);
        return "admin/employee/role/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") RoleRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("allModules", ALL_MODULES);
            model.addAttribute("role", employeeService.findRoleById(id));
            return "admin/employee/role/edit";
        }
        try {
            employeeService.updateRole(id, req);
            ra.addFlashAttribute("success", "Role updated");
            return "redirect:/admin/employees/roles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("allModules", ALL_MODULES);
            model.addAttribute("role", employeeService.findRoleById(id));
            return "admin/employee/role/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        employeeService.deleteRole(id);
        ra.addFlashAttribute("success", "Role deleted");
        return "redirect:/admin/employees/roles";
    }

    @GetMapping("/{id}/status")
    public String toggleStatus(@PathVariable UUID id,
                                @org.springframework.web.bind.annotation.RequestParam boolean status) {
        employeeService.toggleRoleStatus(id, status);
        return "redirect:/admin/employees/roles";
    }
}
