package com.drivemond.admin.controller;

import com.drivemond.admin.dto.ActivityLogFilterRequest;
import com.drivemond.admin.entity.ActivityLog;
import com.drivemond.admin.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Displays the paginated activity log.
 * Route: GET /admin/log
 */
@Controller
@RequestMapping("/admin/log")
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public String log(@ModelAttribute ActivityLogFilterRequest filter, Model model) {
        // logable_type is mandatory in the filter; default to "User" if absent
        if (filter.getLogableType() == null || filter.getLogableType().isBlank()) {
            filter.setLogableType("User");
        }

        Page<ActivityLog> page = activityLogService.findLogs(filter);
        model.addAttribute("logs", page);
        model.addAttribute("filter", filter);
        return "admin/dashboard/activity-log";
    }
}
