package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.AppointmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private Doctor testDoctor;
    private Patient testPatient;
    private DoctorAvailability testAvailability;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setFirstName("John");
        testDoctor.setLastName("Doe");

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("Jane");
        testPatient.setLastName("Smith");

        testAppointment = new Appointment();
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        testAppointment.setAppointmentDate(LocalDate.of(2024, 12, 15));
        testAppointment.setStartTime(LocalTime.of(10, 0));
        testAppointment.setEndTime(LocalTime.of(10, 30));

        testAvailability = new DoctorAvailability();
        testAvailability.setDoctor(testDoctor);
        testAvailability.setDayOfWeek("FRIDAY");
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));
    }

    // Book Appointment Tests
    @Test
    void bookAppointment_Success() {
        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(List.of(testAvailability));
        when(appointmentRepository.findConflictingAppointments(
                anyString(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.bookAppointment(testAppointment);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("SCHEDULED");
        assertThat(result.getAppointmentId()).startsWith("APT-");
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_DoctorNotAvailable_ThrowsException() {
        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> appointmentService.bookAppointment(testAppointment))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor is not available at the requested time");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void bookAppointment_TimeSlotConflict_ThrowsException() {
        Appointment conflictingAppointment = new Appointment();
        conflictingAppointment.setAppointmentId("APT-002");

        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(List.of(testAvailability));
        when(appointmentRepository.findConflictingAppointments(
                anyString(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(List.of(conflictingAppointment));

        assertThatThrownBy(() -> appointmentService.bookAppointment(testAppointment))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Time slot conflicts with existing appointment");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void bookAppointment_AppointmentStartsAtAvailabilityStart_Success() {
        testAppointment.setStartTime(LocalTime.of(9, 0));
        testAppointment.setEndTime(LocalTime.of(9, 30));

        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(List.of(testAvailability));
        when(appointmentRepository.findConflictingAppointments(
                anyString(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.bookAppointment(testAppointment);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void bookAppointment_AppointmentEndsAtAvailabilityEnd_Success() {
        testAppointment.setStartTime(LocalTime.of(16, 30));
        testAppointment.setEndTime(LocalTime.of(17, 0));

        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(List.of(testAvailability));
        when(appointmentRepository.findConflictingAppointments(
                anyString(), any(LocalDate.class), any(LocalTime.class), any(LocalTime.class)))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.bookAppointment(testAppointment);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    // Get Available Slots Tests
//     @Test
//     void getAvailableSlots_NoBookedAppointments_ReturnsAllSlots() {
//         LocalDate date = LocalDate.of(2024, 12, 15);
//         testAvailability.setStartTime(LocalTime.of(9, 0));
//         testAvailability.setEndTime(LocalTime.of(10, 0));

//         when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
//                 .thenReturn(List.of(testAvailability));
//         when(appointmentRepository.findByDoctorIdAndDate(anyString(), any(LocalDate.class)))
//                 .thenReturn(Collections.emptyList());

//         List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

//         assertThat(slots).hasSize(2);
//         assertThat(slots.get(0).getStartTime()).isEqualTo(LocalTime.of(9, 0));
//         assertThat(slots.get(0).getEndTime()).isEqualTo(LocalTime.of(9, 30));
//         assertThat(slots.get(1).getStartTime()).isEqualTo(LocalTime.of(9, 30));
//         assertThat(slots.get(1).getEndTime()).isEqualTo(LocalTime.of(10, 0));
//     }

//     @Test
//     void getAvailableSlots_WithBookedAppointments_ExcludesBookedSlots() {
//         LocalDate date = LocalDate.of(2024, 12, 15);
//         testAvailability.setStartTime(LocalTime.of(9, 0));
//         testAvailability.setEndTime(LocalTime.of(11, 0));

//         Appointment bookedAppointment = new Appointment();
//         bookedAppointment.setStartTime(LocalTime.of(9, 30));
//         bookedAppointment.setEndTime(LocalTime.of(10, 0));
//         bookedAppointment.setStatus("SCHEDULED");

//         when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
//                 .thenReturn(List.of(testAvailability));
//         when(appointmentRepository.findByDoctorIdAndDate(anyString(), any(LocalDate.class)))
//                 .thenReturn(List.of(bookedAppointment));

//         List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

//         assertThat(slots).hasSize(3);
//         assertThat(slots).noneMatch(slot -> 
//             slot.getStartTime().equals(LocalTime.of(9, 30)));
//     }

    @Test
    void getAvailableSlots_CancelledAppointments_IncludesThoseSlots() {
        LocalDate date = LocalDate.of(2024, 12, 15);
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(10, 0));

        Appointment cancelledAppointment = new Appointment();
        cancelledAppointment.setStartTime(LocalTime.of(9, 0));
        cancelledAppointment.setEndTime(LocalTime.of(9, 30));
        cancelledAppointment.setStatus("CANCELLED");

        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(List.of(testAvailability));
        when(appointmentRepository.findByDoctorIdAndDate(anyString(), any(LocalDate.class)))
                .thenReturn(List.of(cancelledAppointment));

        List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

        assertThat(slots).hasSize(2);
    }

    @Test
    void getAvailableSlots_NoAvailability_ReturnsEmptyList() {
        LocalDate date = LocalDate.of(2024, 12, 15);

        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

        assertThat(slots).isEmpty();
    }

    @Test
    void getAvailableSlots_MultipleAvailabilityBlocks_CombinesSlots() {
        LocalDate date = LocalDate.of(2024, 12, 15);
        
        DoctorAvailability morning = new DoctorAvailability();
        morning.setStartTime(LocalTime.of(9, 0));
        morning.setEndTime(LocalTime.of(10, 0));

        DoctorAvailability afternoon = new DoctorAvailability();
        afternoon.setStartTime(LocalTime.of(14, 0));
        afternoon.setEndTime(LocalTime.of(15, 0));

        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(List.of(morning, afternoon));
        when(appointmentRepository.findByDoctorIdAndDate(anyString(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots("DOC-001", date);

        assertThat(slots).hasSize(4);
    }

    // Update Appointment Tests
    @Test
    void updateAppointment_AllFields_Success() {
        Appointment updatedData = new Appointment();
        updatedData.setAppointmentDate(LocalDate.of(2024, 12, 16));
        updatedData.setStartTime(LocalTime.of(11, 0));
        updatedData.setEndTime(LocalTime.of(11, 30));
        updatedData.setType("Follow-up");
        updatedData.setStatus("CONFIRMED");
        
        Doctor newDoctor = new Doctor();
        newDoctor.setPersonId("DOC-002");
        updatedData.setDoctor(newDoctor);

        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.updateAppointment("APT-001", updatedData);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_PartialUpdate_Success() {
        Appointment updatedData = new Appointment();
        updatedData.setStatus("CONFIRMED");

        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.updateAppointment("APT-001", updatedData);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_AppointmentNotFound_ThrowsException() {
        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.updateAppointment("APT-999", new Appointment()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateAppointment_NullFields_DoesNotUpdate() {
        Appointment updatedData = new Appointment();

        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.updateAppointment("APT-001", updatedData);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(testAppointment);
    }

    // Cancel Appointment Tests
    @Test
    void cancelAppointment_Success() {
        testAppointment.setAppointmentId("APT-001");
        testAppointment.setStatus("SCHEDULED");

        when(appointmentRepository.findById("APT-001"))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        appointmentService.cancelAppointment("APT-001");

        verify(appointmentRepository).save(testAppointment);
        assertThat(testAppointment.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelAppointment_AppointmentNotFound_ThrowsException() {
        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.cancelAppointment("APT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_AlreadyCancelled_StillUpdates() {
        testAppointment.setStatus("CANCELLED");

        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        appointmentService.cancelAppointment("APT-001");

        verify(appointmentRepository).save(testAppointment);
    }

    // Get Appointment Tests
    @Test
    void getAppointmentById_Success() {
        when(appointmentRepository.findById("APT-001"))
                .thenReturn(Optional.of(testAppointment));

        Appointment result = appointmentService.getAppointmentById("APT-001");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testAppointment);
    }

    @Test
    void getAppointmentById_NotFound_ThrowsException() {
        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById("APT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");
    }

    @Test
    void getAppointmentsByPatient_Success() {
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByPatientId("PAT-001"))
                .thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByPatient("PAT-001");

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testAppointment);
    }

    @Test
    void getAppointmentsByPatient_NoAppointments_ReturnsEmptyList() {
        when(appointmentRepository.findByPatientId(anyString()))
                .thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointmentsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void getAppointmentsByDoctor_Success() {
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByDoctorId("DOC-001"))
                .thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDoctor("DOC-001");

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(testAppointment);
    }

    @Test
    void getAppointmentsByDate_Success() {
        LocalDate date = LocalDate.of(2024, 12, 15);
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByAppointmentDate(date))
                .thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDate(date);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAppointmentsByStatus_Success() {
        List<Appointment> appointments = List.of(testAppointment);
        when(appointmentRepository.findByStatus("SCHEDULED"))
                .thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByStatus("SCHEDULED");

        assertThat(result).hasSize(1);
    }

    // Conduct Consultation Tests
    @Test
    void conductConsultation_Success() {
        testAppointment.setStatus("SCHEDULED");

        when(appointmentRepository.findById("APT-001"))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.conductConsultation(
                "APT-001", "Flu", "Rest and medication", "Patient recovering well");

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        assertThat(result.getDiagnosis()).isEqualTo("Flu");
        assertThat(result.getTreatmentPlan()).isEqualTo("Rest and medication");
        assertThat(result.getNotes()).isEqualTo("Patient recovering well");
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void conductConsultation_AppointmentNotFound_ThrowsException() {
        when(appointmentRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.conductConsultation(
                "APT-999", "Flu", "Rest", "Notes"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Appointment not found");
    }

    @Test
    void conductConsultation_WithNullValues_Success() {
        when(appointmentRepository.findById("APT-001"))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.conductConsultation(
                "APT-001", null, null, null);

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void conductConsultation_AlreadyCompleted_UpdatesAgain() {
        testAppointment.setStatus("COMPLETED");

        when(appointmentRepository.findById("APT-001"))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        Appointment result = appointmentService.conductConsultation(
                "APT-001", "Updated diagnosis", "Updated plan", "Updated notes");

        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(appointmentRepository).save(testAppointment);
    }
}
