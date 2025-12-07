package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.PatientService;
import com.v322.healthsync.service.PatientService.MedicalHistoryDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;
    private Appointment testAppointment;
    private Prescription testPrescription;
    private Bill testBill;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testPatient.setGender("Male");
        testPatient.setBloodGroup("O+");
        testPatient.setContactNumber("1234567890");
        testPatient.setEmail("john.doe@test.com");
        testPatient.setCity("New York");
        testPatient.setRegistrationDate(LocalDate.now());

        testAppointment = new Appointment();
        testAppointment.setPatient(testPatient);

        testPrescription = new Prescription();
        testPrescription.setPatient(testPatient);

        testBill = new Bill();
        testBill.setPatient(testPatient);
    }

    // Register Patient Tests
    @Test
    void registerPatient_Success() {
        when(patientRepository.save(any(Patient.class)))
                .thenReturn(testPatient);

        Patient result = patientService.registerPatient(testPatient);

        assertThat(result).isNotNull();
        assertThat(result.getRegistrationDate()).isEqualTo(LocalDate.now());
        verify(patientRepository).save(testPatient);
    }

    @Test
    void registerPatient_SetsRegistrationDate() {
        testPatient.setRegistrationDate(null);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> {
                    Patient patient = invocation.getArgument(0);
                    assertThat(patient.getRegistrationDate()).isEqualTo(LocalDate.now());
                    return patient;
                });

        patientService.registerPatient(testPatient);

        verify(patientRepository).save(testPatient);
    }

    @Test
    void registerPatient_WithAllFields_Success() {
        when(patientRepository.save(any(Patient.class)))
                .thenReturn(testPatient);

        Patient result = patientService.registerPatient(testPatient);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getBloodGroup()).isEqualTo("O+");
        assertThat(result.getCity()).isEqualTo("New York");
    }

    // Update Patient Tests
    @Test
    void updatePatient_AllFields_Success() {
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

        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class)))
                .thenReturn(testPatient);

        Patient result = patientService.updatePatient("PAT-001", updateData);

        assertThat(result).isNotNull();
        verify(patientRepository).save(testPatient);
    }

    @Test
    void updatePatient_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                patientService.updatePatient("PAT-999", new Patient()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    @Test
    void updatePatient_PartialUpdate_Success() {
        Patient updateData = new Patient();
        updateData.setFirstName("Jane");
        updateData.setLastName("Smith");

        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class)))
                .thenReturn(testPatient);

        Patient result = patientService.updatePatient("PAT-001", updateData);

        assertThat(result).isNotNull();
        verify(patientRepository).save(testPatient);
    }

    // Get Patient Tests
    @Test
    void getPatientById_Success() {
        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));

        Patient result = patientService.getPatientById("PAT-001");

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("PAT-001");
    }

    @Test
    void getPatientById_NotFound_ThrowsException() {
        when(patientRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getPatientById("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    @Test
    void getPatientByEmail_Success() {
        when(patientRepository.findByEmail("john.doe@test.com"))
                .thenReturn(testPatient);

        Patient result = patientService.getPatientByEmail("john.doe@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@test.com");
    }

    @Test
    void getPatientByEmail_NotFound_ReturnsNull() {
        when(patientRepository.findByEmail(anyString()))
                .thenReturn(null);

        Patient result = patientService.getPatientByEmail("nonexistent@test.com");

        assertThat(result).isNull();
    }

    @Test
    void getAllPatients_Success() {
        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");
        
        List<Patient> patients = Arrays.asList(testPatient, patient2);
        when(patientRepository.findAll())
                .thenReturn(patients);

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testPatient, patient2);
    }

    @Test
    void getAllPatients_NoPatients_ReturnsEmptyList() {
        when(patientRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).isEmpty();
    }

    @Test
    void searchPatientsByName_Success() {
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.searchByName("John"))
                .thenReturn(patients);

        List<Patient> result = patientService.searchPatientsByName("John");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).contains("John");
    }

    @Test
    void searchPatientsByName_NoMatch_ReturnsEmptyList() {
        when(patientRepository.searchByName(anyString()))
                .thenReturn(Collections.emptyList());

        List<Patient> result = patientService.searchPatientsByName("NonExistent");

        assertThat(result).isEmpty();
    }

    @Test
    void getPatientsByCity_Success() {
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findByCity("New York"))
                .thenReturn(patients);

        List<Patient> result = patientService.getPatientsByCity("New York");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("New York");
    }

    @Test
    void getPatientsByCity_NoMatch_ReturnsEmptyList() {
        when(patientRepository.findByCity(anyString()))
                .thenReturn(Collections.emptyList());

        List<Patient> result = patientService.getPatientsByCity("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void getPatientsByBloodGroup_Success() {
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findByBloodGroup("O+"))
                .thenReturn(patients);

        List<Patient> result = patientService.getPatientsByBloodGroup("O+");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBloodGroup()).isEqualTo("O+");
    }

    @Test
    void getPatientsByBloodGroup_NoMatch_ReturnsEmptyList() {
        when(patientRepository.findByBloodGroup(anyString()))
                .thenReturn(Collections.emptyList());

        List<Patient> result = patientService.getPatientsByBloodGroup("AB-");

        assertThat(result).isEmpty();
    }

    // Medical History Tests
    @Test
    void getMedicalHistory_Success() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        List<Prescription> prescriptions = Arrays.asList(testPrescription);
        List<Bill> bills = Arrays.asList(testBill);

        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));
        when(appointmentRepository.findByPatientId("PAT-001"))
                .thenReturn(appointments);
        when(prescriptionRepository.findByPatientId("PAT-001"))
                .thenReturn(prescriptions);
        when(billRepository.findByPatientId("PAT-001"))
                .thenReturn(bills);

        PatientService.MedicalHistory result = patientService.getMedicalHistory("PAT-001");

        assertThat(result).isNotNull();
        assertThat(result.getPatient()).isEqualTo(testPatient);
        assertThat(result.getAppointments()).hasSize(1);
        assertThat(result.getPrescriptions()).hasSize(1);
        assertThat(result.getBills()).hasSize(1);
    }

    @Test
    void getMedicalHistory_NoData_ReturnsEmptyLists() {
        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));
        when(appointmentRepository.findByPatientId("PAT-001"))
                .thenReturn(Collections.emptyList());
        when(prescriptionRepository.findByPatientId("PAT-001"))
                .thenReturn(Collections.emptyList());
        when(billRepository.findByPatientId("PAT-001"))
                .thenReturn(Collections.emptyList());

        PatientService.MedicalHistory result = patientService.getMedicalHistory("PAT-001");

        assertThat(result.getAppointments()).isEmpty();
        assertThat(result.getPrescriptions()).isEmpty();
        assertThat(result.getBills()).isEmpty();
    }

    @Test
    void getMedicalHistory_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getMedicalHistory("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    @Test
    void getMedicalHistoryDTO_Success() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        List<Prescription> prescriptions = Arrays.asList(testPrescription);
        List<Bill> bills = Arrays.asList(testBill);

        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));
        when(appointmentRepository.findByPatientId("PAT-001"))
                .thenReturn(appointments);
        when(prescriptionRepository.findByPatientId("PAT-001"))
                .thenReturn(prescriptions);
        when(billRepository.findByPatientId("PAT-001"))
                .thenReturn(bills);

        MedicalHistoryDTO result = patientService.getMedicalHistoryDTO("PAT-001");

        assertThat(result).isNotNull();
        assertThat(result.getPatient()).isNotNull();
        assertThat(result.getAppointments()).hasSize(1);
        assertThat(result.getPrescriptions()).hasSize(1);
        assertThat(result.getBills()).hasSize(1);
    }

    @Test
    void getMedicalHistoryDTO_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.getMedicalHistoryDTO("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

    // Delete Patient Tests
    @Test
    void deletePatient_Success() {
        doNothing().when(patientRepository).deleteById("PAT-001");

        patientService.deletePatient("PAT-001");

        verify(patientRepository).deleteById("PAT-001");
    }

    @Test
    void deletePatient_NonExistent_NoException() {
        doNothing().when(patientRepository).deleteById("PAT-999");

        patientService.deletePatient("PAT-999");

        verify(patientRepository).deleteById("PAT-999");
    }

    @Test
    void deletePatient_MultipleDeletes_Success() {
        doNothing().when(patientRepository).deleteById(anyString());

        patientService.deletePatient("PAT-001");
        patientService.deletePatient("PAT-002");

        verify(patientRepository, times(2)).deleteById(anyString());
    }
}