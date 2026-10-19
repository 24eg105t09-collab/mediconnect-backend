package com.mediconnect.hospital.config;

import com.mediconnect.hospital.entity.*;
import com.mediconnect.hospital.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (departmentRepository.count() > 0) {
            return; // Already initialized
        }

        System.out.println(">>> Initializing MediConnect AI seed data...");

        // 1. Departments
        Department cardio = departmentRepository.save(new Department("Cardiology", "Heart health, ECG, cardiovascular disease prevention & treatment", "Heart"));
        Department derma = departmentRepository.save(new Department("Dermatology", "Skin disorders, cosmetic treatments, hair and scalp care", "Sparkles"));
        Department pedia = departmentRepository.save(new Department("Pediatrics", "Infant and child wellness, childhood illness care, vaccinations", "Baby"));
        Department ortho = departmentRepository.save(new Department("Orthopedics", "Bone, joint, spine, sports injury and fracture rehabilitation", "Bone"));
        Department neuro = departmentRepository.save(new Department("Neurology", "Brain, spine, nervous system disorders and migraine care", "Activity"));
        Department genMed = departmentRepository.save(new Department("General Medicine", "Primary healthcare, fever, infection treatment, lifestyle health", "Stethoscope"));

        // 2. Admin User
        User adminUser = userRepository.save(new User(
                "admin",
                "admin123",
                "ADMIN",
                "Hospital Administrator",
                "admin@mediconnect.com",
                "9876543200"
        ));

        // 3. Sample Doctors
        // Dr. Ananya - Dermatology
        User u1 = userRepository.save(new User("dr.ananya", "doctor123", "DOCTOR", "Dr. Ananya Sharma", "ananya@mediconnect.com", "9876543201"));
        Doctor docAnanya = doctorRepository.save(new Doctor(
                u1, "Dr. Ananya Sharma", "Consultant Dermatologist", derma,
                "ananya@mediconnect.com", "9876543201", "Room 204", 700, 8.5, "MBBS, MD (Dermatology)"
        ));

        // Dr. Rahul - Cardiology
        User u2 = userRepository.save(new User("dr.rahul", "doctor123", "DOCTOR", "Dr. Rahul Verma", "rahul@mediconnect.com", "9876543202"));
        Doctor docRahul = doctorRepository.save(new Doctor(
                u2, "Dr. Rahul Verma", "Senior Cardiologist", cardio,
                "rahul@mediconnect.com", "9876543202", "Room 305", 1000, 12.0, "MBBS, DM (Cardiology), FACC"
        ));

        // Dr. Priya - Pediatrics
        User u3 = userRepository.save(new User("dr.priya", "doctor123", "DOCTOR", "Dr. Priya Patel", "priya@mediconnect.com", "9876543203"));
        Doctor docPriya = doctorRepository.save(new Doctor(
                u3, "Dr. Priya Patel", "Child Specialist & Pediatrician", pedia,
                "priya@mediconnect.com", "9876543203", "Room 108", 600, 6.0, "MBBS, DCH, DNB (Pediatrics)"
        ));

        // Dr. Vikram - Orthopedics
        User u4 = userRepository.save(new User("dr.vikram", "doctor123", "DOCTOR", "Dr. Vikram Malhotra", "vikram@mediconnect.com", "9876543204"));
        Doctor docVikram = doctorRepository.save(new Doctor(
                u4, "Dr. Vikram Malhotra", "Orthopedic Surgeon", ortho,
                "vikram@mediconnect.com", "9876543204", "Room 412", 800, 10.0, "MBBS, MS (Orthopedics)"
        ));

        // Dr. Sunita - General Medicine
        User u5 = userRepository.save(new User("dr.sunita", "doctor123", "DOCTOR", "Dr. Sunita Rao", "sunita@mediconnect.com", "9876543205"));
        Doctor docSunita = doctorRepository.save(new Doctor(
                u5, "Dr. Sunita Rao", "General Physician", genMed,
                "sunita@mediconnect.com", "9876543205", "Room 102", 500, 7.0, "MBBS, MD (General Medicine)"
        ));

        // 4. Default Schedules (Monday to Saturday, 9 AM to 5 PM)
        List<String> days = List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
        List<Doctor> allDocs = List.of(docAnanya, docRahul, docPriya, docVikram, docSunita);

        for (Doctor doc : allDocs) {
            for (String day : days) {
                scheduleRepository.save(new DoctorSchedule(
                        doc, day, null, LocalTime.of(9, 0), LocalTime.of(17, 0), 30, true
                ));
            }
        }

        // 5. Sample Patients
        User patientUser = userRepository.save(new User(
                "patient",
                "patient123",
                "PATIENT",
                "Jaidev Kumar",
                "jaidev@example.com",
                "9876543210"
        ));
        Patient samplePatient = patientRepository.save(new Patient(
                patientUser, "Jaidev Kumar", 24, "Male", "B+", "9876543210",
                "jaidev@example.com", "123 Green Valley, Bengaluru", "Mild allergy to Penicillin"
        ));

        // 6. Pre-book an illustrative appointment (e.g. today at 09:30 AM with Dr. Ananya)
        LocalDate today = LocalDate.now();
        appointmentRepository.save(new Appointment(
                samplePatient, docAnanya, today, LocalTime.of(9, 30), "CONFIRMED", "Routine skin checkup", 1, 0
        ));

        System.out.println(">>> MediConnect AI seed data successfully loaded!");
    }
}
