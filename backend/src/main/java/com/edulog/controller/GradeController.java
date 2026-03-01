package com.edulog.controller;

import com.edulog.dto.GradeRequest;
import com.edulog.model.Grade;
import com.edulog.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<Grade>> addGrades(@RequestBody GradeRequest request, Authentication auth) {
        List<Grade> grades = gradeService.addGrades(request, auth.getName());
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}")
    public List<Grade> getByStudent(@PathVariable Long studentId) {
        return gradeService.getByStudent(studentId);
    }

    @GetMapping("/student/{studentId}/gpa")
    public Map<String, Double> getGpa(@PathVariable Long studentId) {
        return Map.of("gpa", gradeService.calculateGpa(studentId));
    }

    @GetMapping("/distribution")
    public Map<String, Map<String, Long>> getDistribution() {
        return gradeService.getGradeDistribution();
    }
}
