package com.v322.healthsync.controller;

import com.v322.healthsync.entity.User;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.dto.PatientRegisterDTO;
import com.v322.healthsync.service.AuthService;
import com.v322.healthsync.service.PatientService;
import com.v322.healthsync.dto.EntityMapper;
import com.v322.healthsync.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityMapper entityMapper;

    /**
     * Login endpoint
     * Request body: { "email": "user@example.com", "password": "password123" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("Email and password are required")
                );
            }

            // Authenticate user
            User user = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

            // Get user type
            String userType = authService.getUserType(user);

            // Generate JWT token with user details
            String jwt = jwtService.generateToken(user.getEmail(), userType, user.getPersonId());

            // Create response with token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("userType", userType);
            response.put("userId", user.getPersonId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                createErrorResponse("Invalid email or password")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("An error occurred during login")
            );
        }
    }

    /**
     * Register new patient endpoint
     * Request body: Patient object with all required fields
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody PatientRegisterDTO patient) {
        try {
            // Validate input
            if (patient.getEmail() == null || patient.getPassword() == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("Email and password are required")
                );
            }

            if (patient.getFirstName() == null || patient.getLastName() == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("First name and last name are required")
                );
            }

            // Set registration date
            patient.setRegistrationDate(LocalDate.now());
            patient.setPersonId("PAT-"+UUID.randomUUID().toString());

            // Register patient (password will be hashed in service)
            Patient registeredPatient = authService.registerPatient(entityMapper.toPatientEntity(patient));

            // Generate JWT token
            String jwt = jwtService.generateToken(
                registeredPatient.getEmail(), 
                "PATIENT", 
                registeredPatient.getPersonId()
            );

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("userType", "PATIENT");
            response.put("userId", registeredPatient.getPersonId());
            response.put("email", registeredPatient.getEmail());
            response.put("firstName", registeredPatient.getFirstName());
            response.put("lastName", registeredPatient.getLastName());
            response.put("message", "Registration successful");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Email already registered")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    createErrorResponse("Email already registered")
                );
            }
            return ResponseEntity.badRequest().body(
                createErrorResponse(e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("An error occurred during registration")
            );
        }
    }

    /**
     * Validate token endpoint
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequest tokenRequest) {
        try {
            String token = tokenRequest.getToken();
            
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("Token is required")
                );
            }

            // Validate token
            if (jwtService.validateToken(token)) {
                String email = jwtService.extractEmail(token);
                String userType = jwtService.extractUserType(token);
                String userId = jwtService.extractUserId(token);

                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("email", email);
                response.put("userType", userType);
                response.put("userId", userId);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    createErrorResponse("Invalid or expired token")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                createErrorResponse("Invalid token")
            );
        }
    }

    /**
     * Change password endpoint
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            if (request.getEmail() == null || request.getOldPassword() == null || 
                request.getNewPassword() == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("All fields are required")
                );
            }

            authService.changePassword(request.getEmail(), request.getOldPassword(), 
                                      request.getNewPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                createErrorResponse("Invalid credentials")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("An error occurred while changing password")
            );
        }
    }

    /**
     * Get current user info endpoint (requires valid token in header)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    createErrorResponse("Invalid authorization header")
                );
            }

            String token = authHeader.substring(7);
            
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    createErrorResponse("Invalid or expired token")
                );
            }

            String email = jwtService.extractEmail(token);
            User user = authService.getUserByEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getPersonId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("userType", authService.getUserType(user));
            response.put("contactNumber", user.getContactNumber());
            response.put("city", user.getCity());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("An error occurred while fetching user info")
            );
        }
    }

    // Helper method to create error response
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

    // Request DTOs
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class TokenRequest {
        private String token;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class ChangePasswordRequest {
        private String email;
        private String oldPassword;
        private String newPassword;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}