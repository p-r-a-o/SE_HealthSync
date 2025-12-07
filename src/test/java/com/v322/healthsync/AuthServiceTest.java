package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.UserRepository;
import com.v322.healthsync.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

class AuthServiceTest extends BaseIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

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
        testPatient.setPassword(passwordEncoder.encode("password123"));
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setPassword(passwordEncoder.encode("password123"));

        testReceptionist = new Receptionist();
        testReceptionist.setPersonId("REC-001");
        testReceptionist.setEmail("receptionist@test.com");
        testReceptionist.setPassword(passwordEncoder.encode("password123"));

        testPharmacist = new Pharmacist();
        testPharmacist.setPersonId("PHAR-001");
        testPharmacist.setEmail("pharmacist@test.com");
        testPharmacist.setPassword(passwordEncoder.encode("password123"));
    }

    // Authenticate Tests
    @Test
    void authenticate_ValidCredentials_ReturnsUser() {
        userRepository.save(testPatient);

        User result = authService.authenticate("patient@test.com", "password123");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("patient@test.com");
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        assertThatThrownBy(() -> authService.authenticate("nonexistent@test.com", "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        userRepository.save(testPatient);

        assertThatThrownBy(() -> authService.authenticate("patient@test.com", "wrongPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_EmptyEmail_ThrowsException() {
        assertThatThrownBy(() -> authService.authenticate("", "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_NullEmail_ThrowsException() {
        assertThatThrownBy(() -> authService.authenticate(null, "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_DifferentUserTypes_Success() {
        userRepository.save(testDoctor);

        User result = authService.authenticate("doctor@test.com", "password123");

        assertThat(result).isInstanceOf(Doctor.class);
    }

    // Register Patient Tests
    @Test
    void registerPatient_NewUser_Success() {
        testPatient.setPassword("newPassword123");

        Patient result = authService.registerPatient(testPatient);

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isNotNull();
        
        // Verify password is hashed
        User saved = userRepository.findByEmail("patient@test.com");
        assertThat(passwordEncoder.matches("newPassword123", saved.getPassword())).isTrue();
    }

    @Test
    void registerPatient_EmailAlreadyExists_ThrowsException() {
        userRepository.save(testPatient);

        Patient duplicate = new Patient();
        duplicate.setEmail("patient@test.com");
        duplicate.setPassword("password");

        assertThatThrownBy(() -> authService.registerPatient(duplicate))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");
    }

    @Test
    void registerPatient_WithSpecialCharactersInEmail_Success() {
        testPatient.setEmail("patient+test@example.com");
        testPatient.setPassword("password");

        Patient result = authService.registerPatient(testPatient);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("patient+test@example.com");
    }

    @Test
    void registerPatient_PasswordIsHashed_Success() {
        String rawPassword = "plainPassword";
        testPatient.setPassword(rawPassword);

        Patient result = authService.registerPatient(testPatient);

        User saved = userRepository.findByEmail(result.getEmail());
        assertThat(saved.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, saved.getPassword())).isTrue();
    }

    // Get User By Email Tests
    @Test
    void getUserByEmail_UserExists_ReturnsUser() {
        userRepository.save(testPatient);

        User result = authService.getUserByEmail("patient@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("patient@test.com");
    }

    @Test
    void getUserByEmail_UserNotFound_ThrowsException() {
        assertThatThrownBy(() -> authService.getUserByEmail("nonexistent@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void getUserByEmail_CaseInsensitiveEmail_ReturnsUser() {
        testPatient.setEmail("PATIENT@TEST.COM");
        userRepository.save(testPatient);

        User result = authService.getUserByEmail("PATIENT@TEST.COM");

        assertThat(result).isNotNull();
    }

    // Get User Type Tests
    @Test
    void getUserType_Patient_ReturnsPatient() {
        String type = authService.getUserType(testPatient);
        assertThat(type).isEqualTo("PATIENT");
    }

    @Test
    void getUserType_Doctor_ReturnsDoctor() {
        String type = authService.getUserType(testDoctor);
        assertThat(type).isEqualTo("DOCTOR");
    }

    @Test
    void getUserType_Receptionist_ReturnsReceptionist() {
        String type = authService.getUserType(testReceptionist);
        assertThat(type).isEqualTo("RECEPTIONIST");
    }

    @Test
    void getUserType_Pharmacist_ReturnsPharmacist() {
        String type = authService.getUserType(testPharmacist);
        assertThat(type).isEqualTo("PHARMACIST");
    }

    @Test
    void getUserType_UnknownType_ReturnsUnknown() {
        User unknownUser = new User() {};
        unknownUser.setEmail("unknown@test.com");
        
        String type = authService.getUserType(unknownUser);
        
        assertThat(type).isEqualTo("UNKNOWN");
    }

    // Change Password Tests
    @Test
    void changePassword_ValidOldPassword_Success() {
        userRepository.save(testPatient);
        String oldPassword = "password123";
        String newPassword = "newPassword123";

        authService.changePassword("patient@test.com", oldPassword, newPassword);

        User updated = userRepository.findByEmail("patient@test.com");
        assertThat(passwordEncoder.matches(newPassword, updated.getPassword())).isTrue();
    }

    @Test
    void changePassword_InvalidOldPassword_ThrowsException() {
        userRepository.save(testPatient);

        assertThatThrownBy(() -> 
                authService.changePassword("patient@test.com", "wrongOldPassword", "newPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                authService.changePassword("nonexistent@test.com", "oldPass", "newPass"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void changePassword_SameAsOldPassword_Success() {
        userRepository.save(testPatient);
        String password = "password123";

        authService.changePassword("patient@test.com", password, password);

        User updated = userRepository.findByEmail("patient@test.com");
        assertThat(passwordEncoder.matches(password, updated.getPassword())).isTrue();
    }

    // Reset Password Tests
    @Test
    void resetPassword_UserExists_Success() {
        userRepository.save(testPatient);
        String newPassword = "resetPassword123";

        authService.resetPassword("patient@test.com", newPassword);

        User updated = userRepository.findByEmail("patient@test.com");
        assertThat(passwordEncoder.matches(newPassword, updated.getPassword())).isTrue();
    }

    @Test
    void resetPassword_UserNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                authService.resetPassword("nonexistent@test.com", "newPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void resetPassword_EmptyPassword_EncodesEmptyPassword() {
        userRepository.save(testPatient);

        authService.resetPassword("patient@test.com", "");

        User updated = userRepository.findByEmail("patient@test.com");
        assertThat(passwordEncoder.matches("", updated.getPassword())).isTrue();
    }

    @Test
    void resetPassword_ForDifferentUserTypes_Success() {
        userRepository.save(testDoctor);

        authService.resetPassword("doctor@test.com", "newPassword");

        User updated = userRepository.findByEmail("doctor@test.com");
        assertThat(passwordEncoder.matches("newPassword", updated.getPassword())).isTrue();
    }
}
