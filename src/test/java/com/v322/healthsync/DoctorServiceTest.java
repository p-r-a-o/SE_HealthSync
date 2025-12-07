package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.DoctorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor testDoctor;
    private Department testDepartment;
    private DoctorAvailability testAvailability;
    private Appointment testAppointment;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setDepartmentId("DEPT-001");
        testDepartment.setName("Cardiology");

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setFirstName("John");
        testDoctor.setLastName("Doe");
        testDoctor.setEmail("john.doe@hospital.com");
        testDoctor.setSpecialization("Cardiologist");
        testDoctor.setQualification("MBBS, MD");
        testDoctor.setDepartment(testDepartment);
        testDoctor.setConsultationFee(new BigDecimal("500.00"));

        testAvailability = new DoctorAvailability();
        testAvailability.setSlotId("SLOT-001");
        testAvailability.setDoctor(testDoctor);
        testAvailability.setDayOfWeek("MONDAY");
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("Jane");
        testPatient.setLastName("Smith");

        testAppointment = new Appointment();
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
    }

    // Create Doctor Tests
    @Test
    void createDoctor_Success() {
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(testDoctor);

        Doctor result = doctorService.createDoctor(testDoctor);

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("DOC-001");
        verify(doctorRepository).save(testDoctor);
    }

    @Test
    void createDoctor_WithAllFields_Success() {
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(testDoctor);

        Doctor result = doctorService.createDoctor(testDoctor);

        assertThat(result.getSpecialization()).isEqualTo("Cardiologist");
        assertThat(result.getConsultationFee()).isEqualTo(new BigDecimal("500.00"));
        verify(doctorRepository).save(testDoctor);
    }

    // Get Doctor Tests
    @Test
    void getDoctorById_Success() {
        when(doctorRepository.findById("DOC-001"))
                .thenReturn(Optional.of(testDoctor));

        Doctor result = doctorService.getDoctorById("DOC-001");

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("DOC-001");
    }

    @Test
    void getDoctorById_NotFound_ThrowsException() {
        when(doctorRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.getDoctorById("DOC-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor not found");
    }

    @Test
    void getDoctorByEmail_Success() {
        when(doctorRepository.findByEmail("john.doe@hospital.com"))
                .thenReturn(testDoctor);

        Doctor result = doctorService.getDoctorByEmail("john.doe@hospital.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@hospital.com");
    }

    @Test
    void getDoctorByEmail_NotFound_ReturnsNull() {
        when(doctorRepository.findByEmail(anyString()))
                .thenReturn(null);

        Doctor result = doctorService.getDoctorByEmail("nonexistent@hospital.com");

        assertThat(result).isNull();
    }

    @Test
    void getAllDoctors_Success() {
        Doctor doctor2 = new Doctor();
        doctor2.setPersonId("DOC-002");
        
        List<Doctor> doctors = Arrays.asList(testDoctor, doctor2);
        when(doctorRepository.findAll())
                .thenReturn(doctors);

        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testDoctor, doctor2);
    }

    @Test
    void getAllDoctors_NoDoctors_ReturnsEmptyList() {
        when(doctorRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).isEmpty();
    }

    @Test
    void getDoctorsByDepartment_Success() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findByDepartmentId("DEPT-001"))
                .thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctorsByDepartment("DEPT-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testDoctor);
    }

    @Test
    void getDoctorsBySpecialization_Success() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findBySpecialization("Cardiologist"))
                .thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctorsBySpecialization("Cardiologist");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialization()).isEqualTo("Cardiologist");
    }

    @Test
    void searchDoctorsByName_Success() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.searchByName("John"))
                .thenReturn(doctors);

        List<Doctor> result = doctorService.searchDoctorsByName("John");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void getDoctorsByConsultationFee_Success() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findByConsultationFeeLessThanEqual(new BigDecimal("600.00")))
                .thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctorsByConsultationFee(new BigDecimal("600.00"));

        assertThat(result).hasSize(1);
    }

    // Doctor Availability Tests
    @Test
    void addDoctorAvailability_Success() {
        when(doctorAvailabilityRepository.save(any(DoctorAvailability.class)))
                .thenReturn(testAvailability);

        DoctorAvailability result = doctorService.addDoctorAvailability(testAvailability);

        assertThat(result).isNotNull();
        assertThat(result.getSlotId()).startsWith("SLOT-");
        verify(doctorAvailabilityRepository).save(testAvailability);
    }

    @Test
    void addDoctorAvailability_GeneratesSlotId() {
        when(doctorAvailabilityRepository.save(any(DoctorAvailability.class)))
                .thenReturn(testAvailability);

        DoctorAvailability result = doctorService.addDoctorAvailability(testAvailability);

        assertThat(result.getSlotId()).matches("SLOT-[a-f0-9-]+");
    }

    @Test
    void updateDoctorAvailability_Success() {
        DoctorAvailability updateData = new DoctorAvailability();
        updateData.setStartTime(LocalTime.of(10, 0));
        updateData.setEndTime(LocalTime.of(18, 0));
        updateData.setDayOfWeek("TUESDAY");

        when(doctorAvailabilityRepository.findById("SLOT-001"))
                .thenReturn(Optional.of(testAvailability));
        when(doctorAvailabilityRepository.save(any(DoctorAvailability.class)))
                .thenReturn(testAvailability);

        DoctorAvailability result = doctorService.updateDoctorAvailability("SLOT-001", updateData);

        assertThat(result).isNotNull();
        verify(doctorAvailabilityRepository).save(testAvailability);
    }

    @Test
    void updateDoctorAvailability_NotFound_ThrowsException() {
        when(doctorAvailabilityRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                doctorService.updateDoctorAvailability("SLOT-999", new DoctorAvailability()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Availability slot not found");
    }

    @Test
    void deleteDoctorAvailability_Success() {
        doNothing().when(doctorAvailabilityRepository).deleteById("SLOT-001");

        doctorService.deleteDoctorAvailability("SLOT-001");

        verify(doctorAvailabilityRepository).deleteById("SLOT-001");
    }

    @Test
    void getDoctorAvailability_Success() {
        List<DoctorAvailability> availabilities = Arrays.asList(testAvailability);
        when(doctorAvailabilityRepository.findByDoctorId("DOC-001"))
                .thenReturn(availabilities);

        List<DoctorAvailability> result = doctorService.getDoctorAvailability("DOC-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testAvailability);
    }

    @Test
    void getDoctorAvailabilityByDay_Success() {
        List<DoctorAvailability> availabilities = Arrays.asList(testAvailability);
        when(doctorAvailabilityRepository.findByDoctorIdAndDay("DOC-001", "MONDAY"))
                .thenReturn(availabilities);

        List<DoctorAvailability> result = 
                doctorService.getDoctorAvailabilityByDay("DOC-001", "MONDAY");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeek()).isEqualTo("MONDAY");
    }

    @Test
    void getDoctorAvailabilityByDay_NoDayMatch_ReturnsEmptyList() {
        when(doctorAvailabilityRepository.findByDoctorIdAndDay(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        List<DoctorAvailability> result = 
                doctorService.getDoctorAvailabilityByDay("DOC-001", "SUNDAY");

        assertThat(result).isEmpty();
    }

    // Get Patient List Tests
    @Test
    void getPatientListForDoctor_Success() {
        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");

        Appointment appointment2 = new Appointment();
        appointment2.setDoctor(testDoctor);
        appointment2.setPatient(patient2);

        List<Appointment> appointments = Arrays.asList(testAppointment, appointment2);
        when(appointmentRepository.findByDoctorId("DOC-001"))
                .thenReturn(appointments);

        List<Patient> result = doctorService.getPatientListForDoctor("DOC-001");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testPatient, patient2);
    }

    @Test
    void getPatientListForDoctor_NoAppointments_ReturnsEmptyList() {
        when(appointmentRepository.findByDoctorId(anyString()))
                .thenReturn(Collections.emptyList());

        List<Patient> result = doctorService.getPatientListForDoctor("DOC-001");

        assertThat(result).isEmpty();
    }

    @Test
    void getPatientListForDoctor_DuplicatePatients_ReturnsDistinct() {
        Appointment appointment2 = new Appointment();
        appointment2.setDoctor(testDoctor);
        appointment2.setPatient(testPatient);

        List<Appointment> appointments = Arrays.asList(testAppointment, appointment2);
        when(appointmentRepository.findByDoctorId("DOC-001"))
                .thenReturn(appointments);

        List<Patient> result = doctorService.getPatientListForDoctor("DOC-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPatient);
    }

    // Update Consultation Fee Tests
    @Test
    void updateConsultationFee_Success() {
        BigDecimal newFee = new BigDecimal("750.00");

        when(doctorRepository.findById("DOC-001"))
                .thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(testDoctor);

        Doctor result = doctorService.updateConsultationFee("DOC-001", newFee);

        assertThat(result).isNotNull();
        assertThat(result.getConsultationFee()).isEqualTo(newFee);
        verify(doctorRepository).save(testDoctor);
    }

    @Test
    void updateConsultationFee_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                doctorService.updateConsultationFee("DOC-999", new BigDecimal("500.00")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor not found");
    }

    @Test
    void updateConsultationFee_ZeroFee_Success() {
        when(doctorRepository.findById("DOC-001"))
                .thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(testDoctor);

        Doctor result = doctorService.updateConsultationFee("DOC-001", BigDecimal.ZERO);

        assertThat(result.getConsultationFee()).isEqualTo(BigDecimal.ZERO);
    }

    // Update Doctor Tests
    @Test
    void updateDoctor_AllFields_Success() {
        Doctor updateData = new Doctor();
        updateData.setFirstName("Jane");
        updateData.setLastName("Smith");
        updateData.setContactNumber("9876543210");
        updateData.setEmail("jane.smith@hospital.com");
        updateData.setSpecialization("Neurologist");
        updateData.setQualification("MBBS, MD, DM");
        
        Department newDept = new Department();
        newDept.setDepartmentId("DEPT-002");
        updateData.setDepartment(newDept);
        updateData.setConsultationFee(new BigDecimal("800.00"));

        when(doctorRepository.findById("DOC-001"))
                .thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(testDoctor);

        Doctor result = doctorService.updateDoctor("DOC-001", updateData);

        assertThat(result).isNotNull();
        verify(doctorRepository).save(testDoctor);
    }

    @Test
    void updateDoctor_PartialUpdate_Success() {
        Doctor updateData = new Doctor();
        updateData.setFirstName("Jane");

        when(doctorRepository.findById("DOC-001"))
                .thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(testDoctor);

        Doctor result = doctorService.updateDoctor("DOC-001", updateData);

        assertThat(result).isNotNull();
        verify(doctorRepository).save(testDoctor);
    }

    @Test
    void updateDoctor_NullFields_DoesNotUpdate() {
        Doctor updateData = new Doctor();

        when(doctorRepository.findById("DOC-001"))
                .thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(testDoctor);

        Doctor result = doctorService.updateDoctor("DOC-001", updateData);

        assertThat(result).isNotNull();
        verify(doctorRepository).save(testDoctor);
    }

    @Test
    void updateDoctor_DoctorNotFound_ThrowsException() {
        when(doctorRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                doctorService.updateDoctor("DOC-999", new Doctor()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor not found");
    }

    // Delete Doctor Tests
    @Test
    void deleteDoctor_Success() {
        doNothing().when(doctorRepository).deleteById("DOC-001");

        doctorService.deleteDoctor("DOC-001");

        verify(doctorRepository).deleteById("DOC-001");
    }

    @Test
    void deleteDoctor_NonExistent_NoException() {
        doNothing().when(doctorRepository).deleteById("DOC-999");

        doctorService.deleteDoctor("DOC-999");

        verify(doctorRepository).deleteById("DOC-999");
    }
}