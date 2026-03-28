package com.drivedash.admin.controller;

import com.drivedash.admin.service.AdminNotificationService;
import com.drivedash.admin.service.DashboardStatsService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Serves the admin dashboard and its AJAX data endpoints.
 *
 * <p>Routes:
 * <ul>
 *   <li>GET /admin/dashboard          – main dashboard page</li>
 *   <li>GET /admin/recent-trips        – AJAX: recent trip activity fragment</li>
 *   <li>GET /admin/leader-board-driver – AJAX: driver leaderboard fragment</li>
 *   <li>GET /admin/leader-board-customer – AJAX: customer leaderboard fragment</li>
 *   <li>GET /admin/earning-statistics  – AJAX: JSON earnings chart data</li>
 * </ul>
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','EMPLOYEE')")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardStatsService dashboardStatsService;
    private final AdminNotificationService notificationService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardStatsService.buildStats());
        model.addAttribute("unseenCount", notificationService.countUnseen());
        return "admin/dashboard/index";
    }

    /** AJAX – last 10 trips as JSON for the recent-activity panel. */
    @GetMapping("/recent-trips")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> recentTripActivity() {
        return ResponseEntity.ok(dashboardStatsService.getRecentTrips());
    }

    /** AJAX – top 5 drivers by completed trip count. */
    @GetMapping("/leader-board-driver")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> leaderBoardDriver() {
        return ResponseEntity.ok(dashboardStatsService.getDriverLeaderboard());
    }

    /** AJAX – top 5 customers by total trip count. */
    @GetMapping("/leader-board-customer")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> leaderBoardCustomer() {
        return ResponseEntity.ok(dashboardStatsService.getCustomerLeaderboard());
    }

    /** AJAX – monthly earnings for the current year (12 data points for Chart.js). */
    @GetMapping("/earning-statistics")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> earningStatistics() {
        return ResponseEntity.ok(dashboardStatsService.getMonthlyEarnings());
    }
}