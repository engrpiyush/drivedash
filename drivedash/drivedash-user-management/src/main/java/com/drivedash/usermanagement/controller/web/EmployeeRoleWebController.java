package com.drivedash.usermanagement.controller.web;

import com.drivedash.usermanagement.dto.RoleRequest;
import com.drivedash.usermanagement.service.EmployeeService;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    /** Automatically populate allModules for every request handled by this controller. */
    @ModelAttribute("allModules")
    public List<String> populateModules() {
        return ALL_MODULES;
    }

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page,
                        Model model) {
        model.addAttribute("roles", employeeService.getRolePage(page, 15));
        return "admin/employee/role/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("req", new RoleRequest());
        return "admin/employee/role/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") RoleRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "admin/employee/role/create";
        }
        try {
            employeeService.createRole(req);
            ra.addFlashAttribute("success", "Role created");
            return "redirect:/admin/employees/roles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/employee/role/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        com.drivedash.auth.entity.Role role = employeeService.findRoleById(id);
        RoleRequest req = new RoleRequest();
        req.setName(role.getName());
        req.setModules(role.getModules());
        req.setActive(role.isActive());
        model.addAttribute("role", role);
        model.addAttribute("req", req);
        return "admin/employee/role/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") RoleRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("role", employeeService.findRoleById(id));
            return "admin/employee/role/edit";
        }
        try {
            employeeService.updateRole(id, req);
            ra.addFlashAttribute("success", "Role updated");
            return "redirect:/admin/employees/roles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
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
                                @RequestParam boolean status) {
        employeeService.toggleRoleStatus(id, status);
        return "redirect:/admin/employees/roles";
    }
}
