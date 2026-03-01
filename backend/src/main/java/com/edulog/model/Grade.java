package com.edulog.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private String grade; // A, B, C, D, E

    private double gradePoint; // 4.0, 3.0, 2.0, 1.0, 0.0

    private String semester;

    private LocalDate date;

    private String addedBy;

    public Grade() {}

    public Grade(Student student, Subject subject, String grade, String semester, String addedBy) {
        this.student = student;
        this.subject = subject;
        this.grade = grade;
        this.gradePoint = convertGradeToPoint(grade);
        this.semester = semester;
        this.addedBy = addedBy;
        this.date = LocalDate.now();
    }

    public static double convertGradeToPoint(String grade) {
        return switch (grade) {
            case "A" -> 4.0;
            case "B" -> 3.0;
            case "C" -> 2.0;
            case "D" -> 1.0;
            default -> 0.0;
        };
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) {
        this.grade = grade;
        this.gradePoint = convertGradeToPoint(grade);
    }

    public double getGradePoint() { return gradePoint; }
    public void setGradePoint(double gradePoint) { this.gradePoint = gradePoint; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }
}
