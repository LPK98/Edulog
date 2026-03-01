package com.edulog.dto;

import java.util.List;
import java.util.Map;

public class DashboardStats {
    private long totalStudents;
    private long totalTeachers;
    private double averageAttendance;
    private String userName;
    private String userRole;
    private String userPhone;
    private String userAddress;

    // For student dashboard
    private Double gpa;
    private List<SubjectAttendance> subjectAttendances;
    private StudentInfo studentInfo;

    public static class SubjectAttendance {
        private String subjectName;
        private double percentage;

        public SubjectAttendance(String subjectName, double percentage) {
            this.subjectName = subjectName;
            this.percentage = percentage;
        }

        public String getSubjectName() { return subjectName; }
        public double getPercentage() { return percentage; }
    }

    public static class StudentInfo {
        private String name;
        private String batch;
        private String phone;
        private String address;
        private String studentId;

        public StudentInfo(String name, String batch, String phone, String address, String studentId) {
            this.name = name;
            this.batch = batch;
            this.phone = phone;
            this.address = address;
            this.studentId = studentId;
        }

        public String getName() { return name; }
        public String getBatch() { return batch; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getStudentId() { return studentId; }
    }

    // Getters and setters
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalTeachers() { return totalTeachers; }
    public void setTotalTeachers(long totalTeachers) { this.totalTeachers = totalTeachers; }

    public double getAverageAttendance() { return averageAttendance; }
    public void setAverageAttendance(double averageAttendance) { this.averageAttendance = averageAttendance; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserAddress() { return userAddress; }
    public void setUserAddress(String userAddress) { this.userAddress = userAddress; }

    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }

    public List<SubjectAttendance> getSubjectAttendances() { return subjectAttendances; }
    public void setSubjectAttendances(List<SubjectAttendance> subjectAttendances) { this.subjectAttendances = subjectAttendances; }

    public StudentInfo getStudentInfo() { return studentInfo; }
    public void setStudentInfo(StudentInfo studentInfo) { this.studentInfo = studentInfo; }
}
