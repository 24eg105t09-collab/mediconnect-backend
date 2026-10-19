package com.mediconnect.hospital.service;

import com.mediconnect.hospital.entity.Doctor;
import com.mediconnect.hospital.entity.DoctorSchedule;
import com.mediconnect.hospital.repository.DoctorRepository;
import com.mediconnect.hospital.repository.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    public List<Doctor> getAllActiveDoctors() {
        return doctorRepository.findByActiveTrue();
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId);
    }

    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.isBlank()) {
            return getAllActiveDoctors();
        }
        return doctorRepository.searchDoctors(query.trim());
    }

    public List<Doctor> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentIdAndActiveTrue(departmentId);
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.findById(id).ifPresent(d -> {
            d.setActive(false);
            doctorRepository.save(d);
        });
    }

    public List<DoctorSchedule> getSchedulesByDoctorId(Long doctorId) {
        return scheduleRepository.findByDoctorIdAndAvailableTrue(doctorId);
    }

    public DoctorSchedule saveSchedule(DoctorSchedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
