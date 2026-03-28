package com.drivemond.usermanagement.controller.web;

import com.drivemond.usermanagement.dto.WithdrawMethodRequest;
import com.drivemond.usermanagement.dto.WithdrawRequestAction;
import com.drivemond.usermanagement.entity.WithdrawMethod;
import com.drivemond.usermanagement.entity.WithdrawRequest;
import com.drivemond.usermanagement.service.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/admin/withdraw")
@RequiredArgsConstructor
public class WithdrawWebController {

    private final WithdrawService withdrawService;

    // ── Methods ───────────────────────────────────────────────────────────────

    @GetMapping("/methods")
    public String methods(Model model) {
        model.addAttribute("methods", withdrawService.getAllMethods());
        return "admin/withdraw/method/index";
    }

    @GetMapping("/methods/create")
    public String createMethod(Model model) {
        model.addAttribute("req", new WithdrawMethodRequest());
        return "admin/withdraw/method/create";
    }

    @PostMapping("/methods")
    public String storeMethod(@Valid @ModelAttribute("req") WithdrawMethodRequest req,
                              BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) return "admin/withdraw/method/create";
        try {
            withdrawService.createMethod(req);
            ra.addFlashAttribute("success", "Withdraw method created");
            return "redirect:/admin/withdraw/methods";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "admin/withdraw/method/create";
        }
    }

    @GetMapping("/methods/{id}/edit")
    public String editMethod(@PathVariable Long id, Model model) {
        WithdrawMethod method = withdrawService.findMethodById(id);
        WithdrawMethodRequest req = new WithdrawMethodRequest();
        req.setMethodName(method.getMethodName());
        req.setDefault(method.isDefault());
        req.setActive(method.isActive());
        model.addAttribute("method", method);
        model.addAttribute("req", req);
        return "admin/withdraw/method/edit";
    }

    @PostMapping("/methods/{id}")
    public String updateMethod(@PathVariable Long id,
                               @Valid @ModelAttribute("req") WithdrawMethodRequest req,
                               BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("method", withdrawService.findMethodById(id));
            return "admin/withdraw/method/edit";
        }
        try {
            withdrawService.updateMethod(id, req);
            ra.addFlashAttribute("success", "Method updated");
            return "redirect:/admin/withdraw/methods";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("method", withdrawService.findMethodById(id));
            return "admin/withdraw/method/edit";
        }
    }

    @PostMapping("/methods/{id}/delete")
    public String deleteMethod(@PathVariable Long id, RedirectAttributes ra) {
        withdrawService.deleteMethod(id);
        ra.addFlashAttribute("success", "Method deleted");
        return "redirect:/admin/withdraw/methods";
    }

    @GetMapping("/methods/{id}/status")
    public String toggleMethodStatus(@PathVariable Long id, @RequestParam boolean status) {
        withdrawService.toggleMethodStatus(id, status);
        return "redirect:/admin/withdraw/methods";
    }

    // ── Requests ──────────────────────────────────────────────────────────────

    @GetMapping("/requests")
    public String requests(
            @RequestParam(defaultValue = "pending") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<WithdrawRequest> requests = withdrawService.getRequests(status, page, 15);
        model.addAttribute("requests", requests);
        model.addAttribute("status", status);
        return "admin/withdraw/request/index";
    }

    @GetMapping("/requests/{id}")
    public String showRequest(@PathVariable Long id, Model model) {
        model.addAttribute("request", withdrawService.findRequestById(id));
        model.addAttribute("action", new WithdrawRequestAction());
        return "admin/withdraw/request/show";
    }

    @PostMapping("/requests/{id}/action")
    public String processRequest(@PathVariable Long id,
                                  @ModelAttribute WithdrawRequestAction action,
                                  RedirectAttributes ra) {
        withdrawService.processRequest(id, action);
        ra.addFlashAttribute("success", action.isApproved() ? "Request approved" : "Request rejected");
        return "redirect:/admin/withdraw/requests";
    }
}
