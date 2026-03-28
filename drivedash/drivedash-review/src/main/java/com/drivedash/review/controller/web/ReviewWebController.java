package com.drivedash.review.controller.web;

import com.drivedash.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class ReviewWebController {

    private final ReviewService reviewService;

    @GetMapping
    public String index(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "all") String tripType,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("reviews", reviewService.getPage(search, rating, tripType, page, 20));
        model.addAttribute("search", search);
        model.addAttribute("rating", rating);
        model.addAttribute("tripType", tripType);
        return "admin/review/index";
    }

    @GetMapping("/user/{userId}")
    public String byUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "true") boolean received,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("reviews", reviewService.getByUser(userId, received, page, 20));
        model.addAttribute("userId", userId);
        model.addAttribute("received", received);
        model.addAttribute("avgRating", reviewService.getAverageRating(userId));
        model.addAttribute("totalReviews", reviewService.getReviewCount(userId));
        return "admin/review/index";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        reviewService.delete(id);
        ra.addFlashAttribute("success", "Review deleted");
        return "redirect:/admin/reviews";
    }
}
