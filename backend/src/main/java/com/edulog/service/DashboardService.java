package com.edulog.service;

import com.edulog.dto.DashboardStats;
import com.edulog.model.Role;
import com.edulog.model.Student;
import com.edulog.model.Subject;
import com.edulog.model.User;
import com.edulog.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;

    public DashboardService(StudentRepository studentRepository, UserRepository userRepository,
                            SubjectRepository subjectRepository, AttendanceRepository attendanceRepository,
                            GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.gradeRepository = gradeRepository;
    }

    public DashboardStats getStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DashboardStats stats = new DashboardStats();
        stats.setTotalStudents(studentRepository.count());
        stats.setTotalTeachers(userRepository.countByRole(Role.TEACHER));
        stats.setUserName(user.getName());
        stats.setUserRole(user.getRole().name());
        stats.setUserPhone(user.getPhone());
        stats.setUserAddress(user.getAddress());

        // Calculate average attendance
        var allAttendance = attendanceRepository.findAll();
        if (!allAttendance.isEmpty()) {
            long present = allAttendance.stream()
                    .filter(a -> a.getStatus() == com.edulog.model.Attendance.AttendanceStatus.PRESENT)
                    .count();
            stats.setAverageAttendance(Math.round((double) present / allAttendance.size() * 1000.0) / 10.0);
        }

        // If student, add student-specific data
        if (user.getRole() == Role.STUDENT && user.getStudent() != null) {
            Student student = user.getStudent();
            Double gpa = gradeRepository.calculateGpaByStudentId(student.getId());
            stats.setGpa(gpa != null ? Math.round(gpa * 1000.0) / 1000.0 : 0.0);

            // Subject attendance percentages
            List<Subject> subjects = subjectRepository.findAll();
            List<DashboardStats.SubjectAttendance> subjectAttendances = new ArrayList<>();
            for (Subject subject : subjects) {
                long total = attendanceRepository.countTotalByStudentAndSubject(student.getId(), subject.getId());
                long presentCount = attendanceRepository.countPresentByStudentAndSubject(student.getId(), subject.getId());
                double pct = total > 0 ? (double) presentCount / total * 100 : 0;
                subjectAttendances.add(new DashboardStats.SubjectAttendance(subject.getName(), Math.round(pct * 10.0) / 10.0));
            }
            stats.setSubjectAttendances(subjectAttendances);

            // Student info
            stats.setStudentInfo(new DashboardStats.StudentInfo(
                    student.getName(), student.getBatch(), student.getPhone(),
                    student.getAddress(), student.getStudentId()));
        }

        return stats;
    }
}
