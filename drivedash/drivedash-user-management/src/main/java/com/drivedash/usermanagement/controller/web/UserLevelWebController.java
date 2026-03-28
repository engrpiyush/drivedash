package com.drivedash.usermanagement.controller.web;

import com.drivedash.usermanagement.dto.UserLevelRequest;
import com.drivedash.usermanagement.entity.UserLevel;
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
@RequestMapping("/admin/user-levels")
@RequiredArgsConstructor
public class UserLevelWebController {

    private final UserLevelService levelService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String userType,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<UserLevel> levels = levelService.getPage(userType, status, page, 15);
        model.addAttribute("levels", levels);
        model.addAttribute("userType", userType);
        model.addAttribute("status", status);
        return "admin/user-level/index";
    }

    @GetMapping("/create")
    public String create(@RequestParam(defaultValue = "customer") String userType, Model model) {
        UserLevelRequest req = new UserLevelRequest();
        req.setUserType(userType);
        model.addAttribute("req", req);
        return "admin/user-level/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute("req") UserLevelRequest req,
                        BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "admin/user-level/create";
        try {
            levelService.create(req);
            ra.addFlashAttribute("success", "Level created successfully");
            return "redirect:/admin/user-levels?userType=" + req.getUserType();
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/user-levels/create?userType=" + req.getUserType();
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable UUID id, Model model) {
        UserLevel level = levelService.findById(id);
        UserLevelRequest req = new UserLevelRequest();
        req.setSequence(level.getSequence());
        req.setName(level.getName());
        req.setUserType(level.getUserType());
        req.setRewardType(level.getRewardType());
        req.setRewardAmount(level.getRewardAmount());
        req.setTargetedRide(level.getTargetedRide());
        req.setTargetedRidePoint(level.getTargetedRidePoint());
        req.setTargetedAmount(level.getTargetedAmount());
        req.setTargetedAmountPoint(level.getTargetedAmountPoint());
        req.setTargetedCancel(level.getTargetedCancel());
        req.setTargetedCancelPoint(level.getTargetedCancelPoint());
        req.setTargetedReview(level.getTargetedReview());
        req.setTargetedReviewPoint(level.getTargetedReviewPoint());
        var access = levelService.getAccess(id, level.getUserType());
        req.setBid(access.isBid());
        req.setSeeDestination(access.isSeeDestination());
        req.setSeeSubtotal(access.isSeeSubtotal());
        req.setSeeLevel(access.isSeeLevel());
        req.setCreateHireRequest(access.isCreateHireRequest());
        model.addAttribute("level", level);
        model.addAttribute("req", req);
        return "admin/user-level/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("req") UserLevelRequest req,
                         BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("level", levelService.findById(id));
            return "admin/user-level/edit";
        }
        try {
            levelService.update(id, req);
            ra.addFlashAttribute("success", "Level updated");
            return "redirect:/admin/user-levels?userType=" + req.getUserType();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("level", levelService.findById(id));
            return "admin/user-level/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        String type = levelService.findById(id).getUserType();
        levelService.delete(id);
        ra.addFlashAttribute("success", "Level deleted");
        return "redirect:/admin/user-levels?userType=" + type;
    }

    @GetMapping("/status")
    public String toggleStatus(@RequestParam UUID id,
                                @RequestParam boolean status,
                                @RequestParam(defaultValue = "all") String userType) {
        levelService.toggleStatus(id, status);
        return "redirect:/admin/user-levels?userType=" + userType;
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String trashed(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("levels", levelService.getTrashed(page, 15));
        return "admin/user-level/trashed";
    }

    @GetMapping("/{id}/restore")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String restore(@PathVariable UUID id, RedirectAttributes ra) {
        levelService.restore(id);
        ra.addFlashAttribute("success", "Level restored");
        return "redirect:/admin/user-levels/trashed";
    }

    @PostMapping("/{id}/permanent")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String permanentDelete(@PathVariable UUID id, RedirectAttributes ra) {
        levelService.permanentDelete(id);
        ra.addFlashAttribute("success", "Level permanently deleted");
        return "redirect:/admin/user-levels/trashed";
    }
}
