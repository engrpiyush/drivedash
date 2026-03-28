package com.drivedash.parcel.controller.web;

import com.drivedash.parcel.dto.ParcelWeightRequest;
import com.drivedash.parcel.service.ParcelWeightService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/parcel/weights")
@RequiredArgsConstructor
public class ParcelWeightWebController {

    private final ParcelWeightService weightService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("weights", weightService.getPage(status, page, 15));
        model.addAttribute("req", new ParcelWeightRequest());
        model.addAttribute("status", status);
        return "admin/parcel/weight/index";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") ParcelWeightRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("weights", weightService.getPage("all", 0, 15));
            model.addAttribute("status", "all");
            return "admin/parcel/weight/index";
        }
        try {
            weightService.create(req);
            ra.addFlashAttribute("success", "Weight range created");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/parcel/weights";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("weight", weightService.findById(id));
        model.addAttribute("req", new ParcelWeightRequest());
        return "admin/parcel/weight/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") ParcelWeightRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("weight", weightService.findById(id));
            return "admin/parcel/weight/edit";
        }
        try {
            weightService.update(id, req);
            ra.addFlashAttribute("success", "Weight range updated");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/parcel/weights";
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                               @RequestParam boolean active,
                               @RequestParam(defaultValue = "all") String status) {
        weightService.toggleStatus(id, active);
        return "redirect:/admin/parcel/weights?status=" + status;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        weightService.delete(id);
        ra.addFlashAttribute("success", "Weight range deleted");
        return "redirect:/admin/parcel/weights";
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(Model model) {
        model.addAttribute("weights", weightService.getTrashed());
        return "admin/parcel/weight/trashed";
    }

    @GetMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        weightService.restore(id);
        ra.addFlashAttribute("success", "Weight range restored");
        return "redirect:/admin/parcel/weights/trashed";
    }

    @PostMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        weightService.permanentDelete(id);
        ra.addFlashAttribute("success", "Weight range permanently deleted");
        return "redirect:/admin/parcel/weights/trashed";
    }
}
