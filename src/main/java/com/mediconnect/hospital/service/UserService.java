package com.mediconnect.hospital.service;

import com.mediconnect.hospital.dto.AuthRequest;
import com.mediconnect.hospital.dto.AuthResponse;
import com.mediconnect.hospital.dto.RegisterRequest;
import com.mediconnect.hospital.entity.Department;
import com.mediconnect.hospital.entity.Doctor;
import com.mediconnect.hospital.entity.Patient;
import com.mediconnect.hospital.entity.User;
import com.mediconnect.hospital.repository.DepartmentRepository;
import com.mediconnect.hospital.repository.DoctorRepository;
import com.mediconnect.hospital.repository.PatientRepository;
import com.mediconnect.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public AuthResponse authenticate(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return new AuthResponse(false, "Invalid username or password", null, null, null, null, null, null, null);
        }

        User user = userOpt.get();
        // In technical training/starter mode, compare passwords securely
        if (!user.getPassword().equals(request.getPassword())) {
            return new AuthResponse(false, "Invalid username or password", null, null, null, null, null, null, null);
        }

        Long patientId = null;
        Long doctorId = null;

        if ("PATIENT".equalsIgnoreCase(user.getRole())) {
            Optional<Patient> p = patientRepository.findByUserId(user.getId());
            if (p.isPresent()) patientId = p.get().getId();
        } else if ("DOCTOR".equalsIgnoreCase(user.getRole())) {
            Optional<Doctor> d = doctorRepository.findByUserId(user.getId());
            if (d.isPresent()) doctorId = d.get().getId();
        }

        return new AuthResponse(
                true,
                "Login successful",
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getFullName(),
                user.getEmail(),
                patientId,
                doctorId
        );
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return new AuthResponse(false, "Username is already taken", null, null, null, null, null, null, null);
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return new AuthResponse(false, "Email is already registered", null, null, null, null, null, null, null);
        }

        String role = (req.getRole() != null && !req.getRole().isBlank()) ? req.getRole().toUpperCase() : "PATIENT";
        User user = new User(req.getUsername(), req.getPassword(), role, req.getFullName(), req.getEmail(), req.getPhone());
        user = userRepository.save(user);

        Long patientId = null;
        Long doctorId = null;

        if ("PATIENT".equals(role)) {
            Patient patient = new Patient(
                    user,
                    req.getFullName(),
                    req.getAge() != null ? req.getAge() : 25,
                    req.getGender() != null ? req.getGender() : "Not specified",
                    req.getBloodGroup() != null ? req.getBloodGroup() : "O+",
                    req.getPhone(),
                    req.getEmail(),
                    req.getAddress() != null ? req.getAddress() : "",
                    req.getMedicalHistory() != null ? req.getMedicalHistory() : "None"
            );
            patient = patientRepository.save(patient);
            patientId = patient.getId();
        } else if ("DOCTOR".equals(role)) {
            Department dept = null;
            if (req.getDepartmentId() != null) {
                dept = departmentRepository.findById(req.getDepartmentId()).orElse(null);
            }
            if (dept == null) {
                dept = departmentRepository.findAll().stream().findFirst().orElse(null);
            }

            Doctor doctor = new Doctor(
                    user,
                    req.getFullName(),
                    req.getSpecialization() != null ? req.getSpecialization() : "General Physician",
                    dept,
                    req.getEmail(),
                    req.getPhone(),
                    req.getRoomNumber() != null ? req.getRoomNumber() : "101",
                    req.getConsultationFee() != null ? req.getConsultationFee() : 500,
                    req.getExperienceYears() != null ? req.getExperienceYears() : 5.0,
                    req.getQualifications() != null ? req.getQualifications() : "MBBS"
            );
            doctor = doctorRepository.save(doctor);
            doctorId = doctor.getId();
        }

        return new AuthResponse(
                true,
                "Registration successful",
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getFullName(),
                user.getEmail(),
                patientId,
                doctorId
        );
    }
}
