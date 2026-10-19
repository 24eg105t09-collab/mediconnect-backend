package com.mediconnect.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedules")
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(nullable = false)
    private String dayOfWeek; // e.g. "MONDAY", "TUESDAY", ...

    private LocalDate specificDate; // optional, when schedule is set for a specific date

    @Column(nullable = false)
    private LocalTime startTime; // e.g. 09:00

    @Column(nullable = false)
    private LocalTime endTime; // e.g. 17:00

    private Integer slotDurationMinutes = 30;

    private boolean available = true;

    public DoctorSchedule() {}

    public DoctorSchedule(Doctor doctor, String dayOfWeek, LocalDate specificDate, LocalTime startTime, LocalTime endTime, Integer slotDurationMinutes, boolean available) {
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.specificDate = specificDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDurationMinutes = slotDurationMinutes != null ? slotDurationMinutes : 30;
        this.available = available;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalDate getSpecificDate() { return specificDate; }
    public void setSpecificDate(LocalDate specificDate) { this.specificDate = specificDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(Integer slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
