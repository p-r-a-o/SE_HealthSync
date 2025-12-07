package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.UserRepository;
import com.v322.healthsync.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Patient testPatient;
    private Doctor testDoctor;
    private Receptionist testReceptionist;
    private Pharmacist testPharmacist;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setEmail("patient@test.com");
        testPatient.setPassword("hashedPassword123");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setPassword("hashedPassword123");

        testReceptionist = new Receptionist();
        testReceptionist.setPersonId("REC-001");
        testReceptionist.setEmail("receptionist@test.com");
        testReceptionist.setPassword("hashedPassword123");

        testPharmacist = new Pharmacist();
        testPharmacist.setPersonId("PHAR-001");
        testPharmacist.setEmail("pharmacist@test.com");
        testPharmacist.setPassword("hashedPassword123");
    }

    // Authenticate Tests
    @Test
    void authenticate_ValidCredentials_ReturnsUser() {
        String rawPassword = "password123";
        
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(passwordEncoder.matches(rawPassword, testPatient.getPassword()))
                .thenReturn(true);

        User result = authService.authenticate("patient@test.com", rawPassword);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("patient@test.com");
        verify(userRepository).findByEmail("patient@test.com");
        verify(passwordEncoder).matches(rawPassword, testPatient.getPassword());
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);

        assertThatThrownBy(() -> authService.authenticate("nonexistent@test.com", "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate("patient@test.com", "wrongPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");

        verify(passwordEncoder).matches("wrongPassword", testPatient.getPassword());
    }

    @Test
    void authenticate_EmptyEmail_ThrowsException() {
        when(userRepository.findByEmail(""))
                .thenReturn(null);

        assertThatThrownBy(() -> authService.authenticate("", "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_NullEmail_ThrowsException() {
        when(userRepository.findByEmail(null))
                .thenReturn(null);

        assertThatThrownBy(() -> authService.authenticate(null, "password"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_DifferentUserTypes_Success() {
        when(userRepository.findByEmail("doctor@test.com"))
                .thenReturn(testDoctor);
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        User result = authService.authenticate("doctor@test.com", "password");

        assertThat(result).isInstanceOf(Doctor.class);
    }

    // Register Patient Tests
    @Test
    void registerPatient_NewUser_Success() {
        String rawPassword = "newPassword123";
        testPatient.setPassword(rawPassword);

        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(null);
        when(passwordEncoder.encode(rawPassword))
                .thenReturn("hashedNewPassword123");
        when(userRepository.save(any(Patient.class)))
                .thenReturn(testPatient);

        Patient result = authService.registerPatient(testPatient);

        assertThat(result).isNotNull();
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(testPatient);
    }

    @Test
    void registerPatient_EmailAlreadyExists_ThrowsException() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);

        assertThatThrownBy(() -> authService.registerPatient(testPatient))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void registerPatient_WithSpecialCharactersInEmail_Success() {
        testPatient.setEmail("patient+test@example.com");
        
        when(userRepository.findByEmail("patient+test@example.com"))
                .thenReturn(null);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(Patient.class)))
                .thenReturn(testPatient);

        Patient result = authService.registerPatient(testPatient);

        assertThat(result).isNotNull();
        verify(userRepository).save(testPatient);
    }

    @Test
    void registerPatient_PasswordIsHashed_Success() {
        String rawPassword = "plainPassword";
        testPatient.setPassword(rawPassword);

        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);
        when(passwordEncoder.encode(rawPassword))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> {
                    Patient patient = invocation.getArgument(0);
                    assertThat(patient.getPassword()).isEqualTo("hashedPassword");
                    return patient;
                });

        authService.registerPatient(testPatient);

        verify(passwordEncoder).encode(rawPassword);
    }

    // Get User By Email Tests
    @Test
    void getUserByEmail_UserExists_ReturnsUser() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);

        User result = authService.getUserByEmail("patient@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("patient@test.com");
    }

    @Test
    void getUserByEmail_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);

        assertThatThrownBy(() -> authService.getUserByEmail("nonexistent@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void getUserByEmail_CaseInsensitiveEmail_ReturnsUser() {
        when(userRepository.findByEmail("PATIENT@TEST.COM"))
                .thenReturn(testPatient);

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
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";

        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(passwordEncoder.matches(oldPassword, testPatient.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(newPassword))
                .thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testPatient);

        authService.changePassword("patient@test.com", oldPassword, newPassword);

        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testPatient);
    }

    @Test
    void changePassword_InvalidOldPassword_ThrowsException() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThatThrownBy(() -> 
                authService.changePassword("patient@test.com", "wrongOldPassword", "newPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);

        assertThatThrownBy(() -> 
                authService.changePassword("nonexistent@test.com", "oldPass", "newPass"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void changePassword_SameAsOldPassword_Success() {
        String password = "samePassword123";

        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(passwordEncoder.matches(password, testPatient.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode(password))
                .thenReturn("hashedSamePassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testPatient);

        authService.changePassword("patient@test.com", password, password);

        verify(userRepository).save(testPatient);
    }

    // Reset Password Tests
    @Test
    void resetPassword_UserExists_Success() {
        String newPassword = "resetPassword123";

        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(passwordEncoder.encode(newPassword))
                .thenReturn("hashedResetPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testPatient);

        authService.resetPassword("patient@test.com", newPassword);

        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testPatient);
    }

    @Test
    void resetPassword_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(null);

        assertThatThrownBy(() -> 
                authService.resetPassword("nonexistent@test.com", "newPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void resetPassword_EmptyPassword_EncodesEmptyPassword() {
        when(userRepository.findByEmail("patient@test.com"))
                .thenReturn(testPatient);
        when(passwordEncoder.encode(""))
                .thenReturn("hashedEmptyPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testPatient);

        authService.resetPassword("patient@test.com", "");

        verify(passwordEncoder).encode("");
        verify(userRepository).save(testPatient);
    }

    @Test
    void resetPassword_ForDifferentUserTypes_Success() {
        when(userRepository.findByEmail("doctor@test.com"))
                .thenReturn(testDoctor);
        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testDoctor);

        authService.resetPassword("doctor@test.com", "newPassword");

        verify(userRepository).save(testDoctor);
    }
}
