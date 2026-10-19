package com.mediconnect.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailableSlotDto {
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String departmentName;
    private LocalDate date;
    private LocalTime time;
    private boolean available;
    private Integer consultationFee;
    private Integer estimatedWaitMinutes;

    public AvailableSlotDto() {}

    public AvailableSlotDto(Long doctorId, String doctorName, String specialization, String departmentName, LocalDate date, LocalTime time, boolean available, Integer consultationFee, Integer estimatedWaitMinutes) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.departmentName = departmentName;
        this.date = date;
        this.time = time;
        this.available = available;
        this.consultationFee = consultationFee;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Integer getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Integer consultationFee) { this.consultationFee = consultationFee; }

    public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }
}
