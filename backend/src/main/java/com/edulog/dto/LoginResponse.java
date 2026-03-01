package com.edulog.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private String name;
    private String role;
    private String username;
    private Long studentId;

    public LoginResponse(String token, Long userId, String name, String role, String username, Long studentId) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.username = username;
        this.studentId = studentId;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
    public Long getStudentId() { return studentId; }
}
