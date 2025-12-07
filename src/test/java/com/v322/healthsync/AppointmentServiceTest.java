package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.AppointmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class AppointmentServiceTest extends BaseIntegrationTest {

    @Autowired 
    AppointmentRepository appointmentRepository;

    @Autowired 
    PatientRepository patientRepository;
    
    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Autowired 
    AppointmentService appointmentService;
    
    @Autowired
    DepartmentRepository departmentRepository;

    private Appointment testAppointment;
    private Doctor testDoctor;
    private Patient testPatient;
    private DoctorAvailability testAvailability;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        // Clean up database
        appointmentRepository.deleteAll();
        doctorAvailabilityRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        departmentRepository.deleteAll();
        
        // Create test department
        testDepartment = new Department();
        testDepartment.setDepartmentId("DEPT-001");
        testDepartment.setName("Cardiology");
        testDepartment.setLocation("Building A");
        testDepartment = departmentRepository.save(testDepartment);

        // Create test doctor
        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setFirstName("John");
        testDoctor.setLastName("Doe");
        testDoctor.setEmail("john.doe@test.com");
        testDoctor.setPassword("password");
        testDoctor.setDepartment(testDepartment);
        testDoctor = doctorRepository.save(testDoctor);

        // Create test patient
        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("Jane");
        testPatient.setLastName("Smith");
        testPatient.setEmail("jane.smith@test.com");
        testPatient.setPassword("password");
        testPatient = patientRepository.save(testPatient);

        // Create test appointment
        testAppointment = new Appointment();
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        testAppointment.setAppointmentDate(LocalDate.of(2024, 12, 15));
        testAppointment.setStartTime(LocalTime.of(10, 0));
        testAppointment.setEndTime(LocalTime.of(10, 30));

        // Create test availability
        testAvailability = new DoctorAvailability();
        testAvailability.setDoctor(testDoctor);
        testAvailability.setDayOfWeek("FRIDAY");
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));
        testAvailability = doctorAvailabilityRepository.save(testAvailability);
    }

    // Book Appointment Tests
    @Test
    void bookAppointment_Success() {
        Appointment result = appointmentService.bookAppointment(testAppointment);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("SCHEDULED");
        assertThat(result.getAppointmentId()).startsWith("APT-");
        
        Appointment saved = appointmentRepository.findById(result.getAppointmentId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void bookAppointment_DoctorNotAvailable_ThrowsException() {
        testAppointment.setAppointmentDate(LocalDate.of(2024, 12, 16)); // Monday, no availability

        assertThatThrownBy(() -> appointmentService.bookAppointment(testAppointment))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor is not available at the requested time");
    }

    @Test
    void bookAppointment_TimeSlotConflict_ThrowsException() {
        // Book first appointment
        appointmentService.bookAppointment(testAppointment);

        // Try to book conflicting appointment
        Appointment conflicting = new Appointment();
        conflicting.setDoctor(testDoctor);
        conflicting.setPatient(testPatient);
        conflicting.setAppointmentDate(LocalDate.of(2024, 12, 15));
        conflicting.setStartTime(LocalTime.of(10, 0));
        conflicting.setEndTime(LocalTime.of(10, 30));

        assertThatThrownBy(() -> appointmentService.bookAppointment(conflicting))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Time slot conflicts with existing appointment");
    }

    @Test
    void bookAppointment_AppointmentStartsAtAvailabilityStart_Success() {
        testAppointment.setStartTime(LocalTime.of(9, 0));
        testAppointment.setEndTime(LocalTime.of(9, 30));

        Appointment result = appointmentService.bookAppointment(testAppointment);

        assertThat(result).isNotNull();
    }

    @Test
    void bookAppointment_AppointmentEndsAtAvailabilityEnd_Success() {
        testAppointment.setStartTime(LocalTime.of(16, 30));
        testAppointment.setEndTime(LocalTime.of(17, 0));

        Appointment result = appointmentService.bookAppointment(testAppointment);

        assertThat(result).isNotNull();
    }

    // Get Available Slots Tests
    @Test
    void getAvailableSlots_CancelledAppointments_IncludesThoseSlots() {
        LocalDate date = LocalDate.of(2024, 12, 15);
        
        // Book and cancel an appointment
        Appointment cancelled = new Appointment();
        cancelled.setDoctor(testDoctor);
        cancelled.setPatient(testPatient);
        cancelled.setAppointmentDate(date);
        cancelled.setStartTime(LocalTime.of(9, 0));
        cancelled.setEndTime(LocalTime.of(9, 30));
        cancelled.setStatus("SCHEDULED");
        cancelled = appointmentRepository.save(cancelled);
        
        appointmentService.cancelAppointment(cancelled.getAppointmentId());

        List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

        assertThat(slots).isNotEmpty();
    }

    @Test
    void getAvailableSlots_NoAvailability_ReturnsEmptyList() {
        LocalDate date = LocalDate.of(2024, 12, 16); // Monday, no availability

        List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

        assertThat(slots).isEmpty();
    }

    @Test
    void getAvailableSlots_MultipleAvailabilityBlocks_CombinesSlots() {
        LocalDate date = LocalDate.of(2024, 12, 15);
        
        // Add afternoon availability
        DoctorAvailability afternoon = new DoctorAvailability();
        afternoon.setDoctor(testDoctor);
        afternoon.setDayOfWeek("FRIDAY");
        afternoon.setStartTime(LocalTime.of(14, 0));
        afternoon.setEndTime(LocalTime.of(15, 0));
        doctorAvailabilityRepository.save(afternoon);

        List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

        assertThat(slots.size()).isGreaterThan(2);
    }

    // Update Appointment Tests
    @Test
    void updateAppointment_AllFields_Success() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);
        
        Appointment updatedData = new Appointment();
        updatedData.setAppointmentDate(LocalDate.of(2024, 12, 16));
        updatedData.setStartTime(LocalTime.of(11, 0));
        updatedData.setEndTime(LocalTime.of(11, 30));
        updatedData.setType("Follow-up");
        updatedData.setStatus("CONFIRMED");
        
        Doctor newDoctor = new Doctor();
        newDoctor.setPersonId("DOC-002");
        newDoctor.setFirstName("New");
        newDoctor.setLastName("Doctor");
        newDoctor.setEmail("new.doctor@test.com");
        newDoctor.setPassword("password");
        newDoctor.setDepartment(testDepartment);
        newDoctor = doctorRepository.save(newDoctor);
        updatedData.setDoctor(newDoctor);

        Appointment result = appointmentService.updateAppointment(saved.getAppointmentId(), updatedData);

        assertThat(result).isNotNull();
        assertThat(result.getAppointmentDate()).isEqualTo(LocalDate.of(2024, 12, 16));
    }

    @Test
    void updateAppointment_PartialUpdate_Success() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);
        
        Appointment updatedData = new Appointment();
        updatedData.setStatus("CONFIRMED");

        Appointment result = appointmentService.updateAppointment(saved.getAppointmentId(), updatedData);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void updateAppointment_AppointmentNotFound_ThrowsException() {
        assertThatThrownBy(() -> appointmentService.updateAppointment("APT-999", new Appointment()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");
    }

    @Test
    void updateAppointment_NullFields_DoesNotUpdate() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);
        LocalDate originalDate = saved.getAppointmentDate();
        
        Appointment updatedData = new Appointment();

        Appointment result = appointmentService.updateAppointment(saved.getAppointmentId(), updatedData);

        assertThat(result.getAppointmentDate()).isEqualTo(originalDate);
    }

    // Cancel Appointment Tests
    @Test
    void cancelAppointment_Success() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);

        appointmentService.cancelAppointment(saved.getAppointmentId());

        Appointment cancelled = appointmentRepository.findById(saved.getAppointmentId()).orElse(null);
        assertThat(cancelled).isNotNull();
        assertThat(cancelled.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelAppointment_AppointmentNotFound_ThrowsException() {
        assertThatThrownBy(() -> appointmentService.cancelAppointment("APT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");
    }

    @Test
    void cancelAppointment_AlreadyCancelled_StillUpdates() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);
        appointmentService.cancelAppointment(saved.getAppointmentId());

        appointmentService.cancelAppointment(saved.getAppointmentId());

        Appointment cancelled = appointmentRepository.findById(saved.getAppointmentId()).orElse(null);
        assertThat(cancelled.getStatus()).isEqualTo("CANCELLED");
    }

    // Get Appointment Tests
    @Test
    void getAppointmentById_Success() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);

        Appointment result = appointmentService.getAppointmentById(saved.getAppointmentId());

        assertThat(result).isNotNull();
        assertThat(result.getAppointmentId()).isEqualTo(saved.getAppointmentId());
    }

    @Test
    void getAppointmentById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> appointmentService.getAppointmentById("APT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");
    }

    @Test
    void getAppointmentsByPatient_Success() {
        appointmentService.bookAppointment(testAppointment);

        List<Appointment> result = appointmentService.getAppointmentsByPatient("PAT-001");

        assertThat(result).hasSize(1);
    }

    @Test
    void getAppointmentsByPatient_NoAppointments_ReturnsEmptyList() {
        List<Appointment> result = appointmentService.getAppointmentsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void getAppointmentsByDoctor_Success() {
        appointmentService.bookAppointment(testAppointment);

        List<Appointment> result = appointmentService.getAppointmentsByDoctor("DOC-001");

        assertThat(result).hasSize(1);
    }

    @Test
    void getAppointmentsByDate_Success() {
        appointmentService.bookAppointment(testAppointment);
        LocalDate date = LocalDate.of(2024, 12, 15);

        List<Appointment> result = appointmentService.getAppointmentsByDate(date);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAppointmentsByStatus_Success() {
        appointmentService.bookAppointment(testAppointment);

        List<Appointment> result = appointmentService.getAppointmentsByStatus("SCHEDULED");

        assertThat(result).hasSize(1);
    }

    // Conduct Consultation Tests
    @Test
    void conductConsultation_Success() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);

        Appointment result = appointmentService.conductConsultation(
                saved.getAppointmentId(), "Flu", "Rest and medication", "Patient recovering well");

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        assertThat(result.getDiagnosis()).isEqualTo("Flu");
        assertThat(result.getTreatmentPlan()).isEqualTo("Rest and medication");
        assertThat(result.getNotes()).isEqualTo("Patient recovering well");
    }

    @Test
    void conductConsultation_AppointmentNotFound_ThrowsException() {
        assertThatThrownBy(() -> appointmentService.conductConsultation(
                "APT-999", "Flu", "Rest", "Notes"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");
    }

    @Test
    void conductConsultation_WithNullValues_Success() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);

        Appointment result = appointmentService.conductConsultation(
                saved.getAppointmentId(), null, null, null);

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void conductConsultation_AlreadyCompleted_UpdatesAgain() {
        Appointment saved = appointmentService.bookAppointment(testAppointment);
        appointmentService.conductConsultation(saved.getAppointmentId(), "First", "First", "First");

        Appointment result = appointmentService.conductConsultation(
                saved.getAppointmentId(), "Updated diagnosis", "Updated plan", "Updated notes");

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        assertThat(result.getDiagnosis()).isEqualTo("Updated diagnosis");
    }
}
