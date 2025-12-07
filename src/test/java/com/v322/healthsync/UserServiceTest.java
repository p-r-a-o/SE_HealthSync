package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.UserRepository;
import com.v322.healthsync.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Patient testPatient;
    private Doctor testDoctor;
    private Receptionist testReceptionist;
    private Pharmacist testPharmacist;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setEmail("patient@test.com");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setFirstName("Jane");
        testDoctor.setLastName("Smith");

        testReceptionist = new Receptionist();
        testReceptionist.setPersonId("REC-001");
        testReceptionist.setEmail("receptionist@test.com");
        testReceptionist.setFirstName("Bob");
        testReceptionist.setLastName("Johnson");

        testPharmacist = new Pharmacist();
        testPharmacist.setPersonId("PHAR-001");
        testPharmacist.setEmail("pharmacist@test.com");
        testPharmacist.setFirstName("Alice");
        testPharmacist.setLastName("Williams");
    }

    // Get All Users Tests
    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testPatient, testDoctor, testReceptionist, testPharmacist);
        when(userRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(4);
        assertThat(result).containsExactlyInAnyOrder(testPatient, testDoctor, testReceptionist, testPharmacist);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_NoUsers_ReturnsEmptyList() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_OnlyPatients_Success() {
        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");
        
        List<User> users = Arrays.asList(testPatient, patient2);
        when(userRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(user -> user instanceof Patient);
    }

    @Test
    void getAllUsers_OnlyDoctors_Success() {
        Doctor doctor2 = new Doctor();
        doctor2.setPersonId("DOC-002");
        
        List<User> users = Arrays.asList(testDoctor, doctor2);
        when(userRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(user -> user instanceof Doctor);
    }

    @Test
    void getAllUsers_MixedUserTypes_Success() {
        List<User> users = Arrays.asList(testPatient, testDoctor);
        when(userRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.stream().anyMatch(user -> user instanceof Patient)).isTrue();
        assertThat(result.stream().anyMatch(user -> user instanceof Doctor)).isTrue();
    }

    @Test
    void getAllUsers_SingleUser_Success() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(testPatient));

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPatient);
    }

    // Get User By Email Tests
    @Test
    void getUserByEmail_PatientExists_ReturnsPatient() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);

        User result = userService.getUserByEmail("patient@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Patient.class);
        assertThat(result.getEmail()).isEqualTo("patient@test.com");
        verify(userRepository).findByEmail("patient@test.com");
    }

    @Test
    void getUserByEmail_DoctorExists_ReturnsDoctor() {
        when(userRepository.findByEmail("doctor@test.com"))
                .thenReturn(testDoctor);

        User result = userService.getUserByEmail("doctor@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Doctor.class);
        assertThat(result.getEmail()).isEqualTo("doctor@test.com");
    }

    @Test
    void getUserByEmail_ReceptionistExists_ReturnsReceptionist() {
        when(userRepository.findByEmail("receptionist@test.com"))
                .thenReturn(testReceptionist);

        User result = userService.getUserByEmail("receptionist@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Receptionist.class);
        assertThat(result.getEmail()).isEqualTo("receptionist@test.com");
    }

    @Test
    void getUserByEmail_PharmacistExists_ReturnsPharmacist() {
        when(userRepository.findByEmail("pharmacist@test.com"))
                .thenReturn(testPharmacist);

        User result = userService.getUserByEmail("pharmacist@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pharmacist.class);
        assertThat(result.getEmail()).isEqualTo("pharmacist@test.com");
    }

    @Test
    void getUserByEmail_UserNotFound_ReturnsNull() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);

        User result = userService.getUserByEmail("nonexistent@test.com");

        assertThat(result).isNull();
        verify(userRepository).findByEmail("nonexistent@test.com");
    }

    @Test
    void getUserByEmail_NullEmail_ReturnsNull() {
        when(userRepository.findByEmail(null))
                .thenReturn(null);

        User result = userService.getUserByEmail(null);

        assertThat(result).isNull();
        verify(userRepository).findByEmail(null);
    }

    @Test
    void getUserByEmail_EmptyEmail_ReturnsNull() {
        when(userRepository.findByEmail(""))
                .thenReturn(null);

        User result = userService.getUserByEmail("");

        assertThat(result).isNull();
        verify(userRepository).findByEmail("");
    }

    @Test
    void getUserByEmail_CaseSensitive_ReturnsNull() {
        when(userRepository.findByEmail("PATIENT@TEST.COM"))
                .thenReturn(null);

        User result = userService.getUserByEmail("PATIENT@TEST.COM");

        assertThat(result).isNull();
    }

    @Test
    void getUserByEmail_WithSpecialCharacters_Success() {
        testPatient.setEmail("patient+test@test.com");
        when(userRepository.findByEmail("patient+test@test.com"))
                .thenReturn(testPatient);

        User result = userService.getUserByEmail("patient+test@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("patient+test@test.com");
    }

    @Test
    void getUserByEmail_MultipleCallsSameEmail_ReturnsConsistently() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);

        User result1 = userService.getUserByEmail("patient@test.com");
        User result2 = userService.getUserByEmail("patient@test.com");

        assertThat(result1).isEqualTo(result2);
        verify(userRepository, times(2)).findByEmail("patient@test.com");
    }

    @Test
    void getUserByEmail_DifferentEmails_ReturnsDifferentUsers() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(userRepository.findByEmail("doctor@test.com"))
                .thenReturn(testDoctor);

        User patientResult = userService.getUserByEmail("patient@test.com");
        User doctorResult = userService.getUserByEmail("doctor@test.com");

        assertThat(patientResult).isInstanceOf(Patient.class);
        assertThat(doctorResult).isInstanceOf(Doctor.class);
        assertThat(patientResult).isNotEqualTo(doctorResult);
    }

    @Test
    void getUserByEmail_VerifyRepositoryInvocation() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);

        userService.getUserByEmail("patient@test.com");

        verify(userRepository, times(1)).findByEmail("patient@test.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_VerifyRepositoryInvocation() {
        when(userRepository.findAll())
                .thenReturn(Arrays.asList(testPatient, testDoctor));

        userService.getAllUsers();

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_LargeNumberOfUsers_Success() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Patient patient = new Patient();
            patient.setPersonId("PAT-" + i);
            patient.setEmail("patient" + i + "@test.com");
            users.add(patient);
        }

        when(userRepository.findAll())
                .thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(100);
        verify(userRepository).findAll();
    }
}