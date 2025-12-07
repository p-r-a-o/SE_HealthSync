package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.UserRepository;
import com.v322.healthsync.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class UserServiceTest extends BaseIntegrationTest {

    @Autowired 
    UserRepository userRepository;
    
    @Autowired
    UserService userService;

    private Patient testPatient;
    private Doctor testDoctor;
    private Receptionist testReceptionist;
    private Pharmacist testPharmacist;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setEmail("patient@test.com");
        testPatient.setPassword("password");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setPassword("password");
        testDoctor.setFirstName("Jane");
        testDoctor.setLastName("Smith");

        testReceptionist = new Receptionist();
        testReceptionist.setPersonId("REC-001");
        testReceptionist.setEmail("receptionist@test.com");
        testReceptionist.setPassword("password");
        testReceptionist.setFirstName("Bob");
        testReceptionist.setLastName("Johnson");

        testPharmacist = new Pharmacist();
        testPharmacist.setPersonId("PHAR-001");
        testPharmacist.setEmail("pharmacist@test.com");
        testPharmacist.setPassword("password");
        testPharmacist.setFirstName("Alice");
        testPharmacist.setLastName("Williams");
    }

    // Get All Users Tests
    @Test
    void getAllUsers_Success() {
        userRepository.save(testPatient);
        userRepository.save(testDoctor);
        userRepository.save(testReceptionist);
        userRepository.save(testPharmacist);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(4);
    }

    @Test
    void getAllUsers_NoUsers_ReturnsEmptyList() {
        List<User> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllUsers_OnlyPatients_Success() {
        userRepository.save(testPatient);
        
        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");
        patient2.setEmail("patient2@test.com");
        patient2.setPassword("password");
        userRepository.save(patient2);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(user -> user instanceof Patient);
    }

    @Test
    void getAllUsers_OnlyDoctors_Success() {
        userRepository.save(testDoctor);
        
        Doctor doctor2 = new Doctor();
        doctor2.setPersonId("DOC-002");
        doctor2.setEmail("doctor2@test.com");
        doctor2.setPassword("password");
        userRepository.save(doctor2);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(user -> user instanceof Doctor);
    }

    @Test
    void getAllUsers_MixedUserTypes_Success() {
        userRepository.save(testPatient);
        userRepository.save(testDoctor);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.stream().anyMatch(user -> user instanceof Patient)).isTrue();
        assertThat(result.stream().anyMatch(user -> user instanceof Doctor)).isTrue();
    }

    @Test
    void getAllUsers_SingleUser_Success() {
        userRepository.save(testPatient);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(Patient.class);
    }

    // Get User By Email Tests
    @Test
    void getUserByEmail_PatientExists_ReturnsPatient() {
        userRepository.save(testPatient);

        User result = userService.getUserByEmail("patient@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Patient.class);
        assertThat(result.getEmail()).isEqualTo("patient@test.com");
    }

    @Test
    void getUserByEmail_DoctorExists_ReturnsDoctor() {
        userRepository.save(testDoctor);

        User result = userService.getUserByEmail("doctor@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Doctor.class);
        assertThat(result.getEmail()).isEqualTo("doctor@test.com");
    }

    @Test
    void getUserByEmail_ReceptionistExists_ReturnsReceptionist() {
        userRepository.save(testReceptionist);

        User result = userService.getUserByEmail("receptionist@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Receptionist.class);
        assertThat(result.getEmail()).isEqualTo("receptionist@test.com");
    }

    @Test
    void getUserByEmail_PharmacistExists_ReturnsPharmacist() {
        userRepository.save(testPharmacist);

        User result = userService.getUserByEmail("pharmacist@test.com");

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Pharmacist.class);
        assertThat(result.getEmail()).isEqualTo("pharmacist@test.com");
    }

    @Test
    void getUserByEmail_UserNotFound_ReturnsNull() {
        User result = userService.getUserByEmail("nonexistent@test.com");

        assertThat(result).isNull();
    }

    @Test
    void getUserByEmail_NullEmail_ReturnsNull() {
        User result = userService.getUserByEmail(null);

        assertThat(result).isNull();
    }

    @Test
    void getUserByEmail_EmptyEmail_ReturnsNull() {
        User result = userService.getUserByEmail("");

        assertThat(result).isNull();
    }

    @Test
    void getUserByEmail_CaseSensitive_ReturnsNull() {
        userRepository.save(testPatient);

        User result = userService.getUserByEmail("PATIENT@TEST.COM");

        assertThat(result).isNull();
    }

    @Test
    void getUserByEmail_WithSpecialCharacters_Success() {
        testPatient.setEmail("patient+test@test.com");
        userRepository.save(testPatient);

        User result = userService.getUserByEmail("patient+test@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("patient+test@test.com");
    }

    @Test
    void getUserByEmail_MultipleCallsSameEmail_ReturnsConsistently() {
        userRepository.save(testPatient);

        User result1 = userService.getUserByEmail("patient@test.com");
        User result2 = userService.getUserByEmail("patient@test.com");

        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1.getPersonId()).isEqualTo(result2.getPersonId());
    }

    @Test
    void getUserByEmail_DifferentEmails_ReturnsDifferentUsers() {
        userRepository.save(testPatient);
        userRepository.save(testDoctor);

        User patientResult = userService.getUserByEmail("patient@test.com");
        User doctorResult = userService.getUserByEmail("doctor@test.com");

        assertThat(patientResult).isInstanceOf(Patient.class);
        assertThat(doctorResult).isInstanceOf(Doctor.class);
        assertThat(patientResult.getPersonId()).isNotEqualTo(doctorResult.getPersonId());
    }

    @Test
    void getAllUsers_LargeNumberOfUsers_Success() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Patient patient = new Patient();
            patient.setPersonId("PAT-" + i);
            patient.setEmail("patient" + i + "@test.com");
            patient.setPassword("password");
            users.add(patient);
        }
        userRepository.saveAll(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(100);
    }
}