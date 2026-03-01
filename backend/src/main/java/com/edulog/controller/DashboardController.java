package com.edulog.controller;

import com.edulog.dto.DashboardStats;
import com.edulog.model.Subject;
import com.edulog.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public DashboardStats getStats(Authentication auth) {
        return dashboardService.getStats(auth.getName());
    }

    @GetMapping("/subjects")
    public List<Subject> getSubjects() {
        return dashboardService.getAllSubjects();
    }
}
