package com.edulog.service;

import com.edulog.dto.AttendanceRequest;
import com.edulog.model.Attendance;
import com.edulog.model.Student;
import com.edulog.model.Subject;
import com.edulog.repository.AttendanceRepository;
import com.edulog.repository.StudentRepository;
import com.edulog.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             SubjectRepository subjectRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Attendance> markAttendance(AttendanceRequest request, String markedBy) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        LocalDate date = LocalDate.parse(request.getDate());

        List<Attendance> records = new ArrayList<>();
        for (AttendanceRequest.AttendanceEntry entry : request.getEntries()) {
            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found: " + entry.getStudentId()));

            Attendance attendance = new Attendance(student, subject, date,
                    Attendance.AttendanceStatus.valueOf(entry.getStatus()), markedBy);
            records.add(attendanceRepository.save(attendance));
        }
        return records;
    }

    public List<Attendance> getByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<Attendance> getByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public Map<String, Double> getAttendancePercentageByStudent(Long studentId) {
        List<Subject> subjects = subjectRepository.findAll();
        Map<String, Double> result = new LinkedHashMap<>();

        for (Subject subject : subjects) {
            long total = attendanceRepository.countTotalByStudentAndSubject(studentId, subject.getId());
            long present = attendanceRepository.countPresentByStudentAndSubject(studentId, subject.getId());
            double percentage = total > 0 ? (double) present / total * 100 : 0;
            result.put(subject.getName(), Math.round(percentage * 10.0) / 10.0);
        }
        return result;
    }

    public double getOverallAttendanceAverage() {
        List<Attendance> all = attendanceRepository.findAll();
        if (all.isEmpty()) return 0;
        long present = all.stream()
                .filter(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT)
                .count();
        return Math.round((double) present / all.size() * 1000.0) / 10.0;
    }
}
