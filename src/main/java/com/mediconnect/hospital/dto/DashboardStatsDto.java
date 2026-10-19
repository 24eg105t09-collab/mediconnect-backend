package com.mediconnect.hospital.dto;

import java.util.Map;

public class DashboardStatsDto {
    private long totalPatients;
    private long totalDoctors;
    private long todayAppointments;
    private long confirmedAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private Map<String, Long> departmentBreakdown;

    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalPatients, long totalDoctors, long todayAppointments, long confirmedAppointments, long completedAppointments, long cancelledAppointments, Map<String, Long> departmentBreakdown) {
        this.totalPatients = totalPatients;
        this.totalDoctors = totalDoctors;
        this.todayAppointments = todayAppointments;
        this.confirmedAppointments = confirmedAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.departmentBreakdown = departmentBreakdown;
    }

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalDoctors() { return totalDoctors; }
    public void setTotalDoctors(long totalDoctors) { this.totalDoctors = totalDoctors; }

    public long getTodayAppointments() { return todayAppointments; }
    public void setTodayAppointments(long todayAppointments) { this.todayAppointments = todayAppointments; }

    public long getConfirmedAppointments() { return confirmedAppointments; }
    public void setConfirmedAppointments(long confirmedAppointments) { this.confirmedAppointments = confirmedAppointments; }

    public long getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; }

    public long getCancelledAppointments() { return cancelledAppointments; }
    public void setCancelledAppointments(long cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }

    public Map<String, Long> getDepartmentBreakdown() { return departmentBreakdown; }
    public void setDepartmentBreakdown(Map<String, Long> departmentBreakdown) { this.departmentBreakdown = departmentBreakdown; }
}
