package com.mediconnect.hospital.controller;

import com.mediconnect.hospital.entity.Doctor;
import com.mediconnect.hospital.entity.DoctorSchedule;
import com.mediconnect.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllActiveDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Doctor> getDoctorByUserId(@PathVariable Long userId) {
        return doctorService.getDoctorByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(doctorService.searchDoctors(query));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Doctor>> getDoctorsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(doctorService.getDoctorsByDepartment(departmentId));
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<DoctorSchedule>> getDoctorSchedules(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getSchedulesByDoctorId(id));
    }

    @PostMapping("/{id}/schedules")
    public ResponseEntity<DoctorSchedule> addDoctorSchedule(@PathVariable Long id, @RequestBody DoctorSchedule schedule) {
        Doctor doc = doctorService.getDoctorById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
        schedule.setDoctor(doc);
        return ResponseEntity.ok(doctorService.saveSchedule(schedule));
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        doctorService.deleteSchedule(scheduleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor updated) {
        Doctor doc = doctorService.getDoctorById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
        doc.setName(updated.getName());
        doc.setSpecialization(updated.getSpecialization());
        doc.setRoomNumber(updated.getRoomNumber());
        doc.setConsultationFee(updated.getConsultationFee());
        doc.setExperienceYears(updated.getExperienceYears());
        doc.setQualifications(updated.getQualifications());
        return ResponseEntity.ok(doctorService.saveDoctor(doc));
    }
}
