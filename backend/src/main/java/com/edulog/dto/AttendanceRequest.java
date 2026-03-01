package com.edulog.dto;

import java.util.List;

public class AttendanceRequest {
    private Long subjectId;
    private String date; // yyyy-MM-dd
    private List<AttendanceEntry> entries;

    public static class AttendanceEntry {
        private Long studentId;
        private String status; // PRESENT, ABSENT, LATE

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<AttendanceEntry> getEntries() { return entries; }
    public void setEntries(List<AttendanceEntry> entries) { this.entries = entries; }
}
