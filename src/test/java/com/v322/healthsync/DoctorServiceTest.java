package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.DoctorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class DoctorServiceTest extends BaseIntegrationTest {

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorService doctorService;

    private Doctor testDoctor;
    private Department testDepartment;
    private DoctorAvailability testAvailability;
    private Appointment testAppointment;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        doctorAvailabilityRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        departmentRepository.deleteAll();

        testDepartment = new Department();
        testDepartment.setDepartmentId("DEPT-001");
        testDepartment.setName("Cardiology");
        testDepartment.setLocation("Building A");
        testDepartment = departmentRepository.save(testDepartment);

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setFirstName("John");
        testDoctor.setLastName("Doe");
        testDoctor.setEmail("john.doe@hospital.com");
        testDoctor.setPassword("password");
        testDoctor.setSpecialization("Cardiologist");
        testDoctor.setQualification("MBBS, MD");
        testDoctor.setDepartment(testDepartment);
        testDoctor.setConsultationFee(new BigDecimal("500.00"));

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("Jane");
        testPatient.setLastName("Smith");
        testPatient.setEmail("jane.smith@test.com");
        testPatient.setPassword("password");

        testAvailability = new DoctorAvailability();
        testAvailability.setDayOfWeek("MONDAY");
        testAvailability.setStartTime(LocalTime.of(9, 0));
        testAvailability.setEndTime(LocalTime.of(17, 0));

        testAppointment = new Appointment();
    }

    // Create Doctor Tests
    @Test
    void createDoctor_Success() {
        Doctor result = doctorService.createDoctor(testDoctor);

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("DOC-001");
        
        Doctor saved = doctorRepository.findById(result.getPersonId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void createDoctor_WithAllFields_Success() {
        Doctor result = doctorService.createDoctor(testDoctor);

        assertThat(result.getSpecialization()).isEqualTo("Cardiologist");
        assertThat(result.getConsultationFee()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    // Get Doctor Tests
    @Test
    void getDoctorById_Success() {
        doctorRepository.save(testDoctor);

        Doctor result = doctorService.getDoctorById("DOC-001");

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("DOC-001");
    }

    @Test
    void getDoctorById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> doctorService.getDoctorById("DOC-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor not found");
    }

    @Test
    void getDoctorByEmail_Success() {
        doctorRepository.save(testDoctor);

        Doctor result = doctorService.getDoctorByEmail("john.doe@hospital.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@hospital.com");
    }

    @Test
    void getDoctorByEmail_NotFound_ReturnsNull() {
        Doctor result = doctorService.getDoctorByEmail("nonexistent@hospital.com");

        assertThat(result).isNull();
    }

    @Test
    void getAllDoctors_Success() {
        doctorRepository.save(testDoctor);
        
        Doctor doctor2 = new Doctor();
        doctor2.setPersonId("DOC-002");
        doctor2.setFirstName("Jane");
        doctor2.setLastName("Smith");
        doctor2.setEmail("jane.smith@hospital.com");
        doctor2.setPassword("password");
        doctor2.setDepartment(testDepartment);
        doctorRepository.save(doctor2);

        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllDoctors_NoDoctors_ReturnsEmptyList() {
        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).isEmpty();
    }

    @Test
    void getDoctorsByDepartment_Success() {
        doctorRepository.save(testDoctor);

        List<Doctor> result = doctorService.getDoctorsByDepartment(testDepartment.getDepartmentId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getDoctorsBySpecialization_Success() {
        doctorRepository.save(testDoctor);

        List<Doctor> result = doctorService.getDoctorsBySpecialization("Cardiologist");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialization()).isEqualTo("Cardiologist");
    }

    @Test
    void searchDoctorsByName_Success() {
        doctorRepository.save(testDoctor);

        List<Doctor> result = doctorService.searchDoctorsByName("John");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void getDoctorsByConsultationFee_Success() {
        doctorRepository.save(testDoctor);

        List<Doctor> result = doctorService.getDoctorsByConsultationFee(new BigDecimal("600.00"));

        assertThat(result).hasSize(1);
    }

    // Doctor Availability Tests
    @Test
    void addDoctorAvailability_Success() {
        doctorRepository.save(testDoctor);
        testAvailability.setDoctor(testDoctor);

        DoctorAvailability result = doctorService.addDoctorAvailability(testAvailability);

        assertThat(result).isNotNull();
        assertThat(result.getSlotId()).startsWith("SLOT-");
        
        DoctorAvailability saved = doctorAvailabilityRepository.findById(result.getSlotId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void addDoctorAvailability_GeneratesSlotId() {
        doctorRepository.save(testDoctor);
        testAvailability.setDoctor(testDoctor);

        DoctorAvailability result = doctorService.addDoctorAvailability(testAvailability);

        assertThat(result.getSlotId()).matches("SLOT-[a-f0-9-]+");
    }

    @Test
    void updateDoctorAvailability_Success() {
        doctorRepository.save(testDoctor);
        testAvailability.setDoctor(testDoctor);
        DoctorAvailability saved = doctorService.addDoctorAvailability(testAvailability);
        
        DoctorAvailability updateData = new DoctorAvailability();
        updateData.setStartTime(LocalTime.of(10, 0));
        updateData.setEndTime(LocalTime.of(18, 0));
        updateData.setDayOfWeek("TUESDAY");

        DoctorAvailability result = doctorService.updateDoctorAvailability(saved.getSlotId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(result.getDayOfWeek()).isEqualTo("TUESDAY");
    }

    @Test
    void updateDoctorAvailability_NotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                doctorService.updateDoctorAvailability("SLOT-999", new DoctorAvailability()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Availability slot not found");
    }

    @Test
    void deleteDoctorAvailability_Success() {
        doctorRepository.save(testDoctor);
        testAvailability.setDoctor(testDoctor);
        DoctorAvailability saved = doctorService.addDoctorAvailability(testAvailability);

        doctorService.deleteDoctorAvailability(saved.getSlotId());

        assertThat(doctorAvailabilityRepository.findById(saved.getSlotId())).isEmpty();
    }

    @Test
    void getDoctorAvailability_Success() {
        doctorRepository.save(testDoctor);
        testAvailability.setDoctor(testDoctor);
        doctorService.addDoctorAvailability(testAvailability);

        List<DoctorAvailability> result = doctorService.getDoctorAvailability(testDoctor.getPersonId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getDoctorAvailabilityByDay_Success() {
        doctorRepository.save(testDoctor);
        testAvailability.setDoctor(testDoctor);
        doctorService.addDoctorAvailability(testAvailability);

        List<DoctorAvailability> result = 
                doctorService.getDoctorAvailabilityByDay(testDoctor.getPersonId(), "MONDAY");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDayOfWeek()).isEqualTo("MONDAY");
    }

    @Test
    void getDoctorAvailabilityByDay_NoDayMatch_ReturnsEmptyList() {
        doctorRepository.save(testDoctor);
        testAvailability.setDoctor(testDoctor);
        doctorService.addDoctorAvailability(testAvailability);

        List<DoctorAvailability> result = 
                doctorService.getDoctorAvailabilityByDay(testDoctor.getPersonId(), "SUNDAY");

        assertThat(result).isEmpty();
    }

    // Get Patient List Tests
    @Test
    void getPatientListForDoctor_Success() {
        doctorRepository.save(testDoctor);
        patientRepository.save(testPatient);
        
        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");
        patient2.setFirstName("Bob");
        patient2.setLastName("Johnson");
        patient2.setEmail("bob.johnson@test.com");
        patient2.setPassword("password");
        patientRepository.save(patient2);

        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        testAppointment.setAppointmentId("APT-001");
        appointmentRepository.save(testAppointment);

        Appointment appointment2 = new Appointment();
        appointment2.setAppointmentId("APT-002");
        appointment2.setDoctor(testDoctor);
        appointment2.setPatient(patient2);
        appointmentRepository.save(appointment2);

        List<Patient> result = doctorService.getPatientListForDoctor(testDoctor.getPersonId());

        assertThat(result).hasSize(2);
    }

    @Test
    void getPatientListForDoctor_NoAppointments_ReturnsEmptyList() {
        doctorRepository.save(testDoctor);

        List<Patient> result = doctorService.getPatientListForDoctor(testDoctor.getPersonId());

        assertThat(result).isEmpty();
    }

    @Test
    void getPatientListForDoctor_DuplicatePatients_ReturnsDistinct() {
        doctorRepository.save(testDoctor);
        patientRepository.save(testPatient);

        testAppointment.setAppointmentId("APT-001");
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        appointmentRepository.save(testAppointment);

        Appointment appointment2 = new Appointment();
        appointment2.setAppointmentId("APT-002");
        appointment2.setDoctor(testDoctor);
        appointment2.setPatient(testPatient);
        appointmentRepository.save(appointment2);

        List<Patient> result = doctorService.getPatientListForDoctor(testDoctor.getPersonId());

        assertThat(result).hasSize(1);
    }

    // Update Consultation Fee Tests
    @Test
    void updateConsultationFee_Success() {
        doctorRepository.save(testDoctor);
        BigDecimal newFee = new BigDecimal("750.00");

        Doctor result = doctorService.updateConsultationFee(testDoctor.getPersonId(), newFee);

        assertThat(result).isNotNull();
        assertThat(result.getConsultationFee()).isEqualByComparingTo(newFee);
    }

    @Test
    void updateConsultationFee_DoctorNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                doctorService.updateConsultationFee("DOC-999", new BigDecimal("500.00")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor not found");
    }

    @Test
    void updateConsultationFee_ZeroFee_Success() {
        doctorRepository.save(testDoctor);

        Doctor result = doctorService.updateConsultationFee(testDoctor.getPersonId(), BigDecimal.ZERO);

        assertThat(result.getConsultationFee()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    // Update Doctor Tests
    @Test
    void updateDoctor_AllFields_Success() {
        doctorRepository.save(testDoctor);
        
        Department newDept = new Department();
        newDept.setDepartmentId("DEPT-002");
        newDept.setName("Neurology");
        newDept.setLocation("Building B");
        newDept = departmentRepository.save(newDept);
        
        Doctor updateData = new Doctor();
        updateData.setFirstName("Jane");
        updateData.setLastName("Smith");
        updateData.setContactNumber("9876543210");
        updateData.setEmail("jane.smith@hospital.com");
        updateData.setSpecialization("Neurologist");
        updateData.setQualification("MBBS, MD, DM");
        updateData.setDepartment(newDept);
        updateData.setConsultationFee(new BigDecimal("800.00"));

        Doctor result = doctorService.updateDoctor(testDoctor.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getSpecialization()).isEqualTo("Neurologist");
    }

    @Test
    void updateDoctor_PartialUpdate_Success() {
        doctorRepository.save(testDoctor);
        
        Doctor updateData = new Doctor();
        updateData.setFirstName("Jane");

        Doctor result = doctorService.updateDoctor(testDoctor.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe"); // unchanged
    }

    @Test
    void updateDoctor_NullFields_DoesNotUpdate() {
        doctorRepository.save(testDoctor);
        String originalFirstName = testDoctor.getFirstName();
        
        Doctor updateData = new Doctor();

        Doctor result = doctorService.updateDoctor(testDoctor.getPersonId(), updateData);

        assertThat(result.getFirstName()).isEqualTo(originalFirstName);
    }

    @Test
    void updateDoctor_DoctorNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                doctorService.updateDoctor("DOC-999", new Doctor()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor not found");
    }

    // Delete Doctor Tests
    @Test
    void deleteDoctor_Success() {
        doctorRepository.save(testDoctor);

        doctorService.deleteDoctor(testDoctor.getPersonId());

        assertThat(doctorRepository.findById(testDoctor.getPersonId())).isEmpty();
    }

    @Test
    void deleteDoctor_NonExistent_NoException() {
        doctorService.deleteDoctor("DOC-999");

        // No exception thrown
    }
}
