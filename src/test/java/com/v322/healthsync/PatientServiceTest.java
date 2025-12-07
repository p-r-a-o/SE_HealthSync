package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.PatientService;
import com.v322.healthsync.service.PatientService.MedicalHistoryDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class PatientServiceTest extends BaseIntegrationTest {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    PatientService patientService;

    private Patient testPatient;
    private Appointment testAppointment;
    private Prescription testPrescription;
    private Bill testBill;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        prescriptionRepository.deleteAll();
        billRepository.deleteAll();
        patientRepository.deleteAll();

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testPatient.setGender("Male");
        testPatient.setBloodGroup("O+");
        testPatient.setContactNumber("1234567890");
        testPatient.setEmail("john.doe@test.com");
        testPatient.setPassword("password");
        testPatient.setCity("New York");
        testPatient.setRegistrationDate(LocalDate.now());

        testAppointment = new Appointment();
        testAppointment.setAppointmentId("APT-001");

        testPrescription = new Prescription();
        testPrescription.setPrescriptionId("PRES-001");

        testBill = new Bill();
        testBill.setBillId("BILL-001");
    }

    // Register Patient Tests
    @Test
    void registerPatient_Success() {
        Patient result = patientService.registerPatient(testPatient);

        assertThat(result).isNotNull();
        assertThat(result.getRegistrationDate()).isEqualTo(LocalDate.now());
        
        Patient saved = patientRepository.findById(result.getPersonId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void registerPatient_SetsRegistrationDate() {
        testPatient.setRegistrationDate(null);

        Patient result = patientService.registerPatient(testPatient);

        assertThat(result.getRegistrationDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void registerPatient_WithAllFields_Success() {
        Patient result = patientService.registerPatient(testPatient);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getBloodGroup()).isEqualTo("O+");
        assertThat(result.getCity()).isEqualTo("New York");
    }

    // Update Patient Tests
    @Test
    void updatePatient_AllFields_Success() {
        Patient saved = patientRepository.save(testPatient);
        
        Patient updateData = new Patient();
        updateData.setFirstName("Jane");
        updateData.setLastName("Smith");
        updateData.setDateOfBirth(LocalDate.of(1995, 5, 15));
        updateData.setGender("Female");
        updateData.setBloodGroup("A+");
        updateData.setContactNumber("9876543210");
        updateData.setEmail("jane.smith@test.com");
        updateData.setCity("Boston");
        updateData.setNotes("Updated notes");

        Patient result = patientService.updatePatient(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getCity()).isEqualTo("Boston");
    }

    @Test
    void updatePatient_PatientNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                patientService.updatePatient("PAT-999", new Patient()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    @Test
    void updatePatient_PartialUpdate_Success() {
        Patient saved = patientRepository.save(testPatient);
        
        Patient updateData = new Patient();
        updateData.setFirstName("Jane");
        updateData.setLastName("Smith");

        Patient result = patientService.updatePatient(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
    }

    // Get Patient Tests
    @Test
    void getPatientById_Success() {
        Patient saved = patientRepository.save(testPatient);

        Patient result = patientService.getPatientById(saved.getPersonId());

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo(saved.getPersonId());
    }

    @Test
    void getPatientById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> patientService.getPatientById("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    @Test
    void getPatientByEmail_Success() {
        patientRepository.save(testPatient);

        Patient result = patientService.getPatientByEmail("john.doe@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@test.com");
    }

    @Test
    void getPatientByEmail_NotFound_ReturnsNull() {
        Patient result = patientService.getPatientByEmail("nonexistent@test.com");

        assertThat(result).isNull();
    }

    @Test
    void getAllPatients_Success() {
        patientRepository.save(testPatient);
        
        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setEmail("jane.smith@test.com");
        patient2.setPassword("password");
        patientRepository.save(patient2);

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllPatients_NoPatients_ReturnsEmptyList() {
        List<Patient> result = patientService.getAllPatients();

        assertThat(result).isEmpty();
    }

    @Test
    void searchPatientsByName_Success() {
        patientRepository.save(testPatient);

        List<Patient> result = patientService.searchPatientsByName("John");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchPatientsByName_NoMatch_ReturnsEmptyList() {
        patientRepository.save(testPatient);

        List<Patient> result = patientService.searchPatientsByName("NonExistent");

        assertThat(result).isEmpty();
    }

    @Test
    void getPatientsByCity_Success() {
        patientRepository.save(testPatient);

        List<Patient> result = patientService.getPatientsByCity("New York");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("New York");
    }

    @Test
    void getPatientsByCity_NoMatch_ReturnsEmptyList() {
        patientRepository.save(testPatient);

        List<Patient> result = patientService.getPatientsByCity("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void getPatientsByBloodGroup_Success() {
        patientRepository.save(testPatient);

        List<Patient> result = patientService.getPatientsByBloodGroup("O+");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBloodGroup()).isEqualTo("O+");
    }

    @Test
    void getPatientsByBloodGroup_NoMatch_ReturnsEmptyList() {
        patientRepository.save(testPatient);

        List<Patient> result = patientService.getPatientsByBloodGroup("AB-");

        assertThat(result).isEmpty();
    }

    // Medical History Tests
    @Test
    void getMedicalHistory_Success() {
        Patient saved = patientRepository.save(testPatient);
        
        testAppointment.setPatient(saved);
        appointmentRepository.save(testAppointment);
        
        testPrescription.setPatient(saved);
        prescriptionRepository.save(testPrescription);
        
        testBill.setPatient(saved);
        billRepository.save(testBill);

        PatientService.MedicalHistory result = patientService.getMedicalHistory(saved.getPersonId());

        assertThat(result).isNotNull();
        assertThat(result.getPatient()).isEqualTo(saved);
        assertThat(result.getAppointments()).hasSize(1);
        assertThat(result.getPrescriptions()).hasSize(1);
        assertThat(result.getBills()).hasSize(1);
    }

    @Test
    void getMedicalHistory_NoData_ReturnsEmptyLists() {
        Patient saved = patientRepository.save(testPatient);

        PatientService.MedicalHistory result = patientService.getMedicalHistory(saved.getPersonId());

        assertThat(result.getAppointments()).isEmpty();
        assertThat(result.getPrescriptions()).isEmpty();
        assertThat(result.getBills()).isEmpty();
    }

    @Test
    void getMedicalHistory_PatientNotFound_ThrowsException() {
        assertThatThrownBy(() -> patientService.getMedicalHistory("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    @Test
    void getMedicalHistoryDTO_Success() {
        Patient saved = patientRepository.save(testPatient);
        
        testAppointment.setPatient(saved);
        appointmentRepository.save(testAppointment);
        
        testPrescription.setPatient(saved);
        prescriptionRepository.save(testPrescription);
        
        testBill.setPatient(saved);
        billRepository.save(testBill);

        MedicalHistoryDTO result = patientService.getMedicalHistoryDTO(saved.getPersonId());

        assertThat(result).isNotNull();
        assertThat(result.getPatient()).isNotNull();
        assertThat(result.getAppointments()).hasSize(1);
        assertThat(result.getPrescriptions()).hasSize(1);
        assertThat(result.getBills()).hasSize(1);
    }

    @Test
    void getMedicalHistoryDTO_PatientNotFound_ThrowsException() {
        assertThatThrownBy(() -> patientService.getMedicalHistoryDTO("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    // Delete Patient Tests
    @Test
    void deletePatient_Success() {
        Patient saved = patientRepository.save(testPatient);

        patientService.deletePatient(saved.getPersonId());

        assertThat(patientRepository.findById(saved.getPersonId())).isEmpty();
    }

    @Test
    void deletePatient_NonExistent_NoException() {
        patientService.deletePatient("PAT-999");

        // No exception thrown
    }

    @Test
    void deletePatient_MultipleDeletes_Success() {
        Patient patient1 = patientRepository.save(testPatient);
        
        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");
        patient2.setEmail("patient2@test.com");
        patient2.setPassword("password");
        patient2 = patientRepository.save(patient2);

        patientService.deletePatient(patient1.getPersonId());
        patientService.deletePatient(patient2.getPersonId());

        assertThat(patientRepository.count()).isEqualTo(0);
    }
}