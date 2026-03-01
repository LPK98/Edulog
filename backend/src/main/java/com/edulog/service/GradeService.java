package com.edulog.service;

import com.edulog.dto.GradeRequest;
import com.edulog.model.Grade;
import com.edulog.model.Student;
import com.edulog.model.Subject;
import com.edulog.repository.GradeRepository;
import com.edulog.repository.StudentRepository;
import com.edulog.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public GradeService(GradeRepository gradeRepository, StudentRepository studentRepository,
                        SubjectRepository subjectRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Grade> addGrades(GradeRequest request, String addedBy) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Grade> savedGrades = new ArrayList<>();
        for (GradeRequest.GradeEntry entry : request.getGrades()) {
            Subject subject = subjectRepository.findById(entry.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + entry.getSubjectId()));

            Grade grade = new Grade(student, subject, entry.getGrade(), request.getSemester(), addedBy);
            savedGrades.add(gradeRepository.save(grade));
        }

        // Update student GPA
        Double gpa = gradeRepository.calculateGpaByStudentId(student.getId());
        if (gpa != null) {
            student.setGpa(Math.round(gpa * 1000.0) / 1000.0);
            studentRepository.save(student);
        }

        return savedGrades;
    }

    public List<Grade> getByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> getBySemester(Long studentId, String semester) {
        return gradeRepository.findByStudentIdAndSemester(studentId, semester);
    }

    public Double calculateGpa(Long studentId) {
        Double gpa = gradeRepository.calculateGpaByStudentId(studentId);
        return gpa != null ? Math.round(gpa * 1000.0) / 1000.0 : 0.0;
    }

    public Map<String, Map<String, Long>> getGradeDistribution() {
        List<Grade> allGrades = gradeRepository.findAll();
        Map<String, Map<String, Long>> distribution = new LinkedHashMap<>();

        for (Grade g : allGrades) {
            String subjectName = g.getSubject().getName();
            distribution.computeIfAbsent(subjectName, k -> new LinkedHashMap<>());
            distribution.get(subjectName).merge(g.getGrade(), 1L, Long::sum);
        }
        return distribution;
    }
}
