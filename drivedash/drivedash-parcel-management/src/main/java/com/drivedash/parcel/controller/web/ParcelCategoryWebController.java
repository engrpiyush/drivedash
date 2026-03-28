package com.drivedash.parcel.controller.web;

import com.drivedash.parcel.dto.ParcelCategoryRequest;
import com.drivedash.parcel.service.ParcelCategoryService;
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
@RequestMapping("/admin/parcel/categories")
@RequiredArgsConstructor
public class ParcelCategoryWebController {

    private final ParcelCategoryService categoryService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("categories", categoryService.getPage(status, search, page, 15));
        model.addAttribute("req", new ParcelCategoryRequest());
        model.addAttribute("status", status);
        model.addAttribute("search", search);
        return "admin/parcel/category/index";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") ParcelCategoryRequest req,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryService.getPage("all", "", 0, 15));
            model.addAttribute("status", "all");
            model.addAttribute("search", "");
            return "admin/parcel/category/index";
        }
        try {
            categoryService.create(req);
            ra.addFlashAttribute("success", "Category created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/parcel/categories";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        model.addAttribute("req", new ParcelCategoryRequest());
        return "admin/parcel/category/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") ParcelCategoryRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("category", categoryService.findById(id));
            return "admin/parcel/category/edit";
        }
        try {
            categoryService.update(id, req);
            ra.addFlashAttribute("success", "Category updated");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/parcel/categories";
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                               @RequestParam boolean active,
                               @RequestParam(defaultValue = "all") String status) {
        categoryService.toggleStatus(id, active);
        return "redirect:/admin/parcel/categories?status=" + status;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        categoryService.delete(id);
        ra.addFlashAttribute("success", "Category deleted");
        return "redirect:/admin/parcel/categories";
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(Model model) {
        model.addAttribute("categories", categoryService.getTrashed());
        return "admin/parcel/category/trashed";
    }

    @GetMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        categoryService.restore(id);
        ra.addFlashAttribute("success", "Category restored");
        return "redirect:/admin/parcel/categories/trashed";
    }

    @PostMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        categoryService.permanentDelete(id);
        ra.addFlashAttribute("success", "Category permanently deleted");
        return "redirect:/admin/parcel/categories/trashed";
    }
}
