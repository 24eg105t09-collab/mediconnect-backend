package com.mediconnect.hospital.service;

import com.mediconnect.hospital.dto.AppointmentRequest;
import com.mediconnect.hospital.dto.AvailableSlotDto;
import com.mediconnect.hospital.dto.ChatRequest;
import com.mediconnect.hospital.dto.ChatResponse;
import com.mediconnect.hospital.entity.Appointment;
import com.mediconnect.hospital.entity.Department;
import com.mediconnect.hospital.entity.Doctor;
import com.mediconnect.hospital.repository.DepartmentRepository;
import com.mediconnect.hospital.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    // Session state memory for multi-turn booking flows
    private final Map<String, Map<String, Object>> sessionState = new HashMap<>();

    public ChatResponse processMessage(ChatRequest req) {
        String msg = (req.getMessage() != null) ? req.getMessage().trim() : "";
        String lower = msg.toLowerCase();
        String sessionId = (req.getSessionId() != null && !req.getSessionId().isBlank())
                ? req.getSessionId()
                : UUID.randomUUID().toString();

        Map<String, Object> state = sessionState.computeIfAbsent(sessionId, k -> new HashMap<>());

        // 1. Greetings & Help
        if (lower.matches(".*\\b(hi|hello|hey|namaste|good morning|good evening|help)\\b.*")) {
            return new ChatResponse(
                    "Hello! 👋 I'm MediConnect AI, your smart hospital assistant. I can help you find specialists, check real-time doctor availability from our hospital database, and book appointments instantly.",
                    "GREETING",
                    List.of("Find a Dermatologist", "Available Doctors Today", "Check Cardiology Slots", "My Appointments"),
                    null,
                    null
            );
        }

        // 2. Direct Booking Command via Chat: e.g. "Book Dr. Ananya at 10:00" or "Book doctor 1 at 10:00"
        if (lower.contains("book") && (lower.contains("am") || lower.contains("pm") || lower.contains(":") || lower.contains("at "))) {
            return handleChatBooking(req, lower, state);
        }

        // 3. User selected a date in ongoing flow (e.g. "tomorrow", "today", "2026-10-20")
        if (state.containsKey("selectedDoctorId") && (lower.contains("today") || lower.contains("tomorrow") || lower.matches(".*\\d{4}-\\d{2}-\\d{2}.*"))) {
            LocalDate chosenDate = parseDateFromText(lower);
            Long docId = (Long) state.get("selectedDoctorId");
            return checkDoctorAvailability(docId, chosenDate, state);
        }

        // 4. Checking specific Doctor by Name: e.g. "Dr. Ananya", "Dr. Rahul", "Is Dr. Priya available?"
        List<Doctor> allDoctors = doctorRepository.findByActiveTrue();
        for (Doctor doc : allDoctors) {
            String docLastName = doc.getName().toLowerCase().replace("dr.", "").replace("dr ", "").trim();
            if (lower.contains(docLastName)) {
                state.put("selectedDoctorId", doc.getId());
                LocalDate targetDate = parseDateFromText(lower);

                List<AvailableSlotDto> slots = appointmentService.getAvailableSlots(doc.getId(), targetDate);
                List<AvailableSlotDto> freeSlots = slots.stream().filter(AvailableSlotDto::isAvailable).limit(6).toList();

                if (freeSlots.isEmpty()) {
                    List<AvailableSlotDto> alternatives = appointmentService.getAlternativeSlots(doc.getId(), targetDate);
                    return new ChatResponse(
                            "Dr. " + doc.getName() + " has no open slots on " + targetDate + ". Here are smart alternative slots suggested for you:",
                            "ALTERNATIVE_SLOTS",
                            alternatives.stream().map(s -> "Book " + s.getDoctorName() + " at " + s.getTime()).limit(4).toList(),
                            alternatives,
                            doc
                    );
                }

                return new ChatResponse(
                        "Found available slots for " + doc.getName() + " (" + doc.getSpecialization() + ") on " + targetDate + ":",
                        "DOCTOR_SLOTS",
                        freeSlots.stream().map(s -> "Book " + doc.getName() + " at " + s.getTime()).limit(4).toList(),
                        freeSlots,
                        doc
                );
            }
        }

        // 5. Symptom / Concern Analysis -> Suggest Department
        String suggestedDept = detectDepartmentFromSymptom(lower);
        if (suggestedDept != null) {
            List<Doctor> matchingDocs = doctorRepository.findBySpecializationContainingIgnoreCaseAndActiveTrue(suggestedDept);
            if (matchingDocs.isEmpty()) {
                matchingDocs = doctorRepository.searchDoctors(suggestedDept);
            }

            LocalDate today = LocalDate.now();
            List<AvailableSlotDto> slots = new ArrayList<>();
            for (Doctor doc : matchingDocs) {
                slots.addAll(appointmentService.getAvailableSlots(doc.getId(), today).stream().filter(AvailableSlotDto::isAvailable).limit(2).toList());
            }

            return new ChatResponse(
                    "Based on your symptoms, we recommend consulting our " + suggestedDept + " department. 🩺 (Please note: This is an automated department recommendation, not a medical diagnosis).\nHere are doctors and live slots available today:",
                    "SYMPTOM_RECOMMENDATION",
                    matchingDocs.stream().map(d -> "Check " + d.getName()).limit(4).toList(),
                    slots,
                    matchingDocs
            );
        }

        // 6. Department / Specialization Search: e.g. "Cardiologist", "Dermatology", "Pediatrics"
        List<Department> departments = departmentRepository.findAll();
        for (Department dept : departments) {
            if (lower.contains(dept.getName().toLowerCase()) || lower.contains(dept.getName().toLowerCase().replace("ology", "ologist"))) {
                List<Doctor> docsInDept = doctorRepository.findByDepartmentIdAndActiveTrue(dept.getId());
                LocalDate date = parseDateFromText(lower);

                List<AvailableSlotDto> slots = new ArrayList<>();
                for (Doctor d : docsInDept) {
                    slots.addAll(appointmentService.getAvailableSlots(d.getId(), date).stream().filter(AvailableSlotDto::isAvailable).limit(3).toList());
                }

                return new ChatResponse(
                        "Here are available specialists in the " + dept.getName() + " department for " + date + ":",
                        "DEPARTMENT_DOCTORS",
                        docsInDept.stream().map(d -> "Check " + d.getName()).limit(4).toList(),
                        slots,
                        docsInDept
                );
            }
        }

        // 7. General Doctor Availability / "Who is available today/tomorrow?"
        if (lower.contains("available") || lower.contains("doctor") || lower.contains("slot")) {
            LocalDate date = parseDateFromText(lower);
            List<Doctor> docs = doctorRepository.findByActiveTrue();
            List<AvailableSlotDto> allFreeSlots = new ArrayList<>();

            for (Doctor d : docs) {
                allFreeSlots.addAll(appointmentService.getAvailableSlots(d.getId(), date).stream().filter(AvailableSlotDto::isAvailable).limit(2).toList());
            }

            return new ChatResponse(
                    "Here are live available appointment slots across our hospital for " + date + ":",
                    "ALL_AVAILABLE_SLOTS",
                    docs.stream().map(d -> "Dr. " + d.getName()).limit(4).toList(),
                    allFreeSlots,
                    docs
            );
        }

        // 8. Patient Appointments Lookup
        if (lower.contains("my appointment") || lower.contains("booked") || lower.contains("status")) {
            if (req.getPatientId() != null) {
                List<Appointment> apps = appointmentService.getPatientAppointments(req.getPatientId());
                if (apps.isEmpty()) {
                    return new ChatResponse("You currently don't have any booked appointments. Would you like to book one now?", "NO_APPOINTMENTS", List.of("Find a Doctor", "Book General Physician"), null, null);
                }
                Appointment nextApp = apps.get(0);
                return new ChatResponse(
                        "Your latest appointment is with " + nextApp.getDoctor().getName() + " (" + nextApp.getDoctor().getSpecialization() + ") on " + nextApp.getAppointmentDate() + " at " + nextApp.getAppointmentTime() + ". Status: " + nextApp.getStatus() + " | Queue Position: #" + nextApp.getQueueNumber() + " (Est. wait: " + nextApp.getEstimatedWaitMinutes() + " mins).",
                        "PATIENT_APPOINTMENT",
                        List.of("Book Another Appointment", "Cancel Appointment #" + nextApp.getId()),
                        null,
                        apps
                );
            } else {
                return new ChatResponse(
                        "Please log in as a patient to view your active appointment details and queue position.",
                        "AUTH_REQUIRED",
                        List.of("Login as Patient", "Find Doctors"),
                        null,
                        null
                );
            }
        }

        // Default Fallback
        return new ChatResponse(
                "I can help you check live doctor availability and book appointments. Try asking:\n• \"Find a dermatologist for tomorrow\"\n• \"Available cardiologists today\"\n• \"I have chest pain and shortness of breath\"\n• \"Book Dr. Ananya at 10:00 AM\"",
                "HELP_OPTIONS",
                List.of("Find Cardiologist", "Find Dermatologist", "Doctors Available Today", "General Physician"),
                null,
                null
        );
    }

    private ChatResponse handleChatBooking(ChatRequest req, String lower, Map<String, Object> state) {
        if (req.getPatientId() == null) {
            return new ChatResponse(
                    "Please log in or register as a patient first to book an appointment with our doctors.",
                    "LOGIN_REQUIRED",
                    List.of("Login", "View Doctors"),
                    null,
                    null
            );
        }

        // Try to identify doctor
        Doctor targetDoctor = null;
        if (state.containsKey("selectedDoctorId")) {
            targetDoctor = doctorRepository.findById((Long) state.get("selectedDoctorId")).orElse(null);
        }

        if (targetDoctor == null) {
            for (Doctor d : doctorRepository.findByActiveTrue()) {
                String lastName = d.getName().toLowerCase().replace("dr.", "").replace("dr ", "").trim();
                if (lower.contains(lastName)) {
                    targetDoctor = d;
                    break;
                }
            }
        }

        if (targetDoctor == null) {
            targetDoctor = doctorRepository.findByActiveTrue().stream().findFirst().orElse(null);
        }

        if (targetDoctor == null) {
            return new ChatResponse("No doctors are currently available in the system.", "ERROR", null, null, null);
        }

        LocalDate date = parseDateFromText(lower);
        LocalTime time = parseTimeFromText(lower);

        if (time == null) {
            time = LocalTime.of(10, 0); // default slot
        }

        AppointmentRequest appReq = new AppointmentRequest();
        appReq.setPatientId(req.getPatientId());
        appReq.setDoctorId(targetDoctor.getId());
        appReq.setAppointmentDate(date);
        appReq.setAppointmentTime(time);
        appReq.setReason("Booked via MediConnect AI Assistant");

        try {
            Appointment confirmed = appointmentService.bookAppointment(appReq);
            state.clear();
            return new ChatResponse(
                    "🎉 Success! Your appointment with " + targetDoctor.getName() + " (" + targetDoctor.getSpecialization() + ") is confirmed for " + date + " at " + time + ".\n• Queue Number: #" + confirmed.getQueueNumber() + "\n• Estimated Waiting Time: ~" + confirmed.getEstimatedWaitMinutes() + " mins\n• Consultation Fee: ₹" + targetDoctor.getConsultationFee(),
                    "BOOKING_CONFIRMED",
                    List.of("View My Appointments", "Ask another question"),
                    null,
                    confirmed
            );
        } catch (Exception e) {
            List<AvailableSlotDto> alternatives = appointmentService.getAlternativeSlots(targetDoctor.getId(), date);
            return new ChatResponse(
                    "⚠️ " + e.getMessage() + "\nHere are alternative open slots:",
                    "SLOT_UNAVAILABLE",
                    alternatives.stream().map(s -> "Book " + s.getDoctorName() + " at " + s.getTime()).limit(4).toList(),
                    alternatives,
                    null
            );
        }
    }

    private ChatResponse checkDoctorAvailability(Long doctorId, LocalDate date, Map<String, Object> state) {
        Doctor doc = doctorRepository.findById(doctorId).orElse(null);
        if (doc == null) return new ChatResponse("Doctor not found.", "ERROR", null, null, null);

        List<AvailableSlotDto> slots = appointmentService.getAvailableSlots(doctorId, date);
        List<AvailableSlotDto> freeSlots = slots.stream().filter(AvailableSlotDto::isAvailable).limit(6).toList();

        return new ChatResponse(
                "Available slots for " + doc.getName() + " on " + date + ":",
                "SLOTS_FOUND",
                freeSlots.stream().map(s -> "Book " + doc.getName() + " at " + s.getTime()).toList(),
                freeSlots,
                doc
        );
    }

    private String detectDepartmentFromSymptom(String text) {
        if (text.matches(".*\\b(skin|rash|acne|itching|eczema|hair fall|dermatologist)\\b.*")) {
            return "Dermatology";
        }
        if (text.matches(".*\\b(heart|chest pain|palpitations|blood pressure|hypertension|cardio|breathless)\\b.*")) {
            return "Cardiology";
        }
        if (text.matches(".*\\b(bone|joint|fracture|back pain|knee pain|spine|orthopedic|arthritis)\\b.*")) {
            return "Orthopedics";
        }
        if (text.matches(".*\\b(baby|child|infant|pediatric|toddler|vaccination)\\b.*")) {
            return "Pediatrics";
        }
        if (text.matches(".*\\b(headache|migraine|brain|nerve|dizziness|seizure|neurology)\\b.*")) {
            return "Neurology";
        }
        if (text.matches(".*\\b(fever|cold|cough|stomach|flu|weakness|headache|general|vomiting)\\b.*")) {
            return "General Medicine";
        }
        return null;
    }

    private LocalDate parseDateFromText(String text) {
        if (text.contains("tomorrow")) {
            return LocalDate.now().plusDays(1);
        }
        if (text.contains("day after tomorrow")) {
            return LocalDate.now().plusDays(2);
        }
        // Match YYYY-MM-DD
        Matcher m = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})").matcher(text);
        if (m.find()) {
            try {
                return LocalDate.parse(m.group(1));
            } catch (Exception ignored) {}
        }
        return LocalDate.now();
    }

    private LocalTime parseTimeFromText(String text) {
        // Match patterns like "10:00 am", "10 am", "2:30 pm", "14:00"
        Matcher m = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?", Pattern.CASE_INSENSITIVE).matcher(text);
        while (m.find()) {
            int hour = Integer.parseInt(m.group(1));
            int minute = (m.group(2) != null) ? Integer.parseInt(m.group(2)) : 0;
            String ampm = m.group(3);

            if (ampm != null) {
                if (ampm.equalsIgnoreCase("pm") && hour < 12) hour += 12;
                if (ampm.equalsIgnoreCase("am") && hour == 12) hour = 0;
            }
            if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                return LocalTime.of(hour, minute);
            }
        }
        return null;
    }
}
