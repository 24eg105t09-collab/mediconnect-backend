package com.mediconnect.hospital.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialization;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private String email;
    private String phone;
    private String roomNumber;
    private Integer consultationFee = 500;
    private Double experienceYears = 5.0;
    private String qualifications;
    private boolean active = true;

    public Doctor() {}

    public Doctor(User user, String name, String specialization, Department department, String email, String phone, String roomNumber, Integer consultationFee, Double experienceYears, String qualifications) {
        this.user = user;
        this.name = name;
        this.specialization = specialization;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.consultationFee = consultationFee;
        this.experienceYears = experienceYears;
        this.qualifications = qualifications;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Integer getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Integer consultationFee) { this.consultationFee = consultationFee; }

    public Double getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Double experienceYears) { this.experienceYears = experienceYears; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
