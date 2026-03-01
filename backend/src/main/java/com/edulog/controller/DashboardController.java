package com.edulog.controller;

import com.edulog.dto.DashboardStats;
import com.edulog.model.Subject;
import com.edulog.repository.SubjectRepository;
import com.edulog.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SubjectRepository subjectRepository;

    public DashboardController(DashboardService dashboardService, SubjectRepository subjectRepository) {
        this.dashboardService = dashboardService;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/stats")
    public DashboardStats getStats(Authentication auth) {
        return dashboardService.getStats(auth.getName());
    }

    @GetMapping("/subjects")
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }
}
