package com.mediconnect.hospital.service;

import com.mediconnect.hospital.dto.AppointmentRequest;
import com.mediconnect.hospital.dto.AvailableSlotDto;
import com.mediconnect.hospital.dto.DashboardStatsDto;
import com.mediconnect.hospital.entity.Appointment;
import com.mediconnect.hospital.entity.Doctor;
import com.mediconnect.hospital.entity.DoctorSchedule;
import com.mediconnect.hospital.entity.Patient;
import com.mediconnect.hospital.repository.AppointmentRepository;
import com.mediconnect.hospital.repository.DoctorRepository;
import com.mediconnect.hospital.repository.DoctorScheduleRepository;
import com.mediconnect.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorScheduleRepository scheduleRepository;

    public List<AvailableSlotDto> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + doctorId));

        String dayOfWeek = date.getDayOfWeek().name();

        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndSpecificDateAndAvailableTrue(doctorId, date);
        if (schedules.isEmpty()) {
            schedules = scheduleRepository.findByDoctorIdAndDayOfWeekIgnoreCaseAndAvailableTrue(doctorId, dayOfWeek);
        }

        // Default schedule if none configured in DB (9 AM to 5 PM)
        if (schedules.isEmpty()) {
            schedules = List.of(new DoctorSchedule(doctor, dayOfWeek, null, LocalTime.of(9, 0), LocalTime.of(17, 0), 30, true));
        }

        List<Appointment> existingAppointments = appointmentRepository.findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(doctorId, date);
        Set<LocalTime> bookedTimes = new HashSet<>();
        for (Appointment app : existingAppointments) {
            if (!"CANCELLED".equalsIgnoreCase(app.getStatus())) {
                bookedTimes.add(app.getAppointmentTime());
            }
        }

        List<AvailableSlotDto> slots = new ArrayList<>();
        int queueCounter = 1;

        for (DoctorSchedule schedule : schedules) {
            LocalTime current = schedule.getStartTime();
            int duration = schedule.getSlotDurationMinutes() != null ? schedule.getSlotDurationMinutes() : 30;

            while (current.isBefore(schedule.getEndTime())) {
                boolean isBooked = bookedTimes.contains(current);
                int waitMinutes = (queueCounter - 1) * 15;

                slots.add(new AvailableSlotDto(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialization(),
                        doctor.getDepartment() != null ? doctor.getDepartment().getName() : "General",
                        date,
                        current,
                        !isBooked,
                        doctor.getConsultationFee(),
                        waitMinutes
                ));

                if (isBooked) {
                    queueCounter++;
                }
                current = current.plusMinutes(duration);
            }
        }

        return slots;
    }

    public List<AvailableSlotDto> getAlternativeSlots(Long doctorId, LocalDate date) {
        List<AvailableSlotDto> currentSlots = getAvailableSlots(doctorId, date);
        List<AvailableSlotDto> freeCurrent = currentSlots.stream().filter(AvailableSlotDto::isAvailable).toList();
        if (!freeCurrent.isEmpty()) {
            return freeCurrent;
        }

        // Try next day
        LocalDate nextDay = date.plusDays(1);
        List<AvailableSlotDto> nextDaySlots = getAvailableSlots(doctorId, nextDay);
        return nextDaySlots.stream().filter(AvailableSlotDto::isAvailable).limit(5).toList();
    }

    @Transactional
    public Appointment bookAppointment(AppointmentRequest req) {
        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + req.getDoctorId()));

        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + req.getPatientId()));

        Optional<Appointment> existing = appointmentRepository.findByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                req.getDoctorId(), req.getAppointmentDate(), req.getAppointmentTime(), "CANCELLED");

        if (existing.isPresent()) {
            throw new RuntimeException("Slot " + req.getAppointmentTime() + " on " + req.getAppointmentDate() +
                    " is already booked. Please choose an alternative slot.");
        }

        long existingCount = appointmentRepository.countConfirmedByDoctorAndDate(doctor.getId(), req.getAppointmentDate());
        int queueNumber = (int) existingCount + 1;
        int estimatedWaitMinutes = (queueNumber - 1) * 15;

        Appointment appointment = new Appointment(
                patient,
                doctor,
                req.getAppointmentDate(),
                req.getAppointmentTime(),
                "CONFIRMED",
                req.getReason() != null ? req.getReason() : "General Consultation",
                queueNumber,
                estimatedWaitMinutes
        );

        return appointmentRepository.save(appointment);
    }

    public Appointment cancelAppointment(Long appointmentId) {
        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));
        app.setStatus("CANCELLED");
        return appointmentRepository.save(app);
    }

    public Appointment updateStatus(Long appointmentId, String newStatus, String notes) {
        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));
        app.setStatus(newStatus);
        if (notes != null) app.setNotes(notes);
        return appointmentRepository.save(app);
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId);
    }

    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescAppointmentTimeAsc(doctorId);
    }

    public List<Appointment> getDoctorAppointmentsForDate(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(doctorId, date);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public DashboardStatsDto getDashboardStats() {
        long totalPatients = patientRepository.count();
        long totalDoctors = doctorRepository.count();
        long todayAppointments = appointmentRepository.countAppointmentsOnDate(LocalDate.now());
        long confirmed = appointmentRepository.countAppointmentsByStatus("CONFIRMED");
        long completed = appointmentRepository.countAppointmentsByStatus("COMPLETED");
        long cancelled = appointmentRepository.countAppointmentsByStatus("CANCELLED");

        Map<String, Long> departmentBreakdown = new HashMap<>();
        List<Object[]> deptCounts = appointmentRepository.countAppointmentsByDepartment();
        for (Object[] row : deptCounts) {
            String deptName = row[0] != null ? row[0].toString() : "Unassigned";
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            departmentBreakdown.put(deptName, count);
        }

        return new DashboardStatsDto(
                totalPatients,
                totalDoctors,
                todayAppointments,
                confirmed,
                completed,
                cancelled,
                departmentBreakdown
        );
    }
}
