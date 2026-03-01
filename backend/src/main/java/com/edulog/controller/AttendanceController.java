package com.edulog.controller;

import com.edulog.dto.AttendanceRequest;
import com.edulog.model.Attendance;
import com.edulog.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<Attendance>> markAttendance(@RequestBody AttendanceRequest request,
                                                            Authentication auth) {
        List<Attendance> records = attendanceService.markAttendance(request, auth.getName());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/student/{studentId}")
    public List<Attendance> getByStudent(@PathVariable Long studentId) {
        return attendanceService.getByStudent(studentId);
    }

    @GetMapping("/student/{studentId}/percentage")
    public Map<String, Double> getPercentage(@PathVariable Long studentId) {
        return attendanceService.getAttendancePercentageByStudent(studentId);
    }

    @GetMapping("/date/{date}")
    public List<Attendance> getByDate(@PathVariable String date) {
        return attendanceService.getByDate(LocalDate.parse(date));
    }
}
