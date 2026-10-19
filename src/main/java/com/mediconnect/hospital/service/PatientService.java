package com.mediconnect.hospital.service;

import com.mediconnect.hospital.entity.Patient;
import com.mediconnect.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }

    public Patient updatePatient(Long id, Patient updated) {
        return patientRepository.findById(id).map(p -> {
            p.setName(updated.getName());
            p.setAge(updated.getAge());
            p.setGender(updated.getGender());
            p.setBloodGroup(updated.getBloodGroup());
            p.setPhone(updated.getPhone());
            p.setEmail(updated.getEmail());
            p.setAddress(updated.getAddress());
            p.setMedicalHistory(updated.getMedicalHistory());
            return patientRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }
}
