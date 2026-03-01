package com.edulog.dto;

import java.util.List;

public class GradeRequest {
    private Long studentId;
    private String semester;
    private List<GradeEntry> grades;

    public static class GradeEntry {
        private Long subjectId;
        private String grade; // A, B, C, D, E

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public List<GradeEntry> getGrades() { return grades; }
    public void setGrades(List<GradeEntry> grades) { this.grades = grades; }
}
