package com.mediconnect.hospital.dto;

public class AuthResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String role;
    private String fullName;
    private String email;
    private Long patientId;
    private Long doctorId;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message, Long userId, String username, String role, String fullName, String email, Long patientId, Long doctorId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
}
