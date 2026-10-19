package com.mediconnect.hospital.dto;

public class ChatRequest {
    private String message;
    private String sessionId;
    private Long patientId;

    public ChatRequest() {}

    public ChatRequest(String message, String sessionId, Long patientId) {
        this.message = message;
        this.sessionId = sessionId;
        this.patientId = patientId;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
}
