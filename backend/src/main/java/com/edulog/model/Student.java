package com.edulog.model;

public class Student {
    private Long id;
    private String studentId;
    private String name;
    private double gpa;

    public Student(){}

    public Student(Long id, String studentId, String name, double gpa){
        this.id = id; this.studentId = studentId; this.name = name; this.gpa = gpa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
}
