package com.mediconnect.hospital.controller;

import com.mediconnect.hospital.dto.DashboardStatsDto;
import com.mediconnect.hospital.service.AppointmentService;
import com.mediconnect.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(appointmentService.getDashboardStats());
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deactivateDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok().build();
    }
}
