package com.v322.healthsync.service;

import com.v322.healthsync.entity.User;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.entity.Doctor;
import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Use constructor injection - this is the proper way and avoids circular dependencies
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user with email and password
     */
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new RuntimeException("Invalid credentials");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    /**
     * Register a new patient
     */
    public Patient registerPatient(Patient patient) {
        // Check if email already exists
        User existingUser = userRepository.findByEmail(patient.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("Email already registered");
        }

        // Hash password before saving
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        
        return userRepository.save(patient);
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    /**
     * Get user type from discriminator value
     */
    public String getUserType(User user) {
        if (user instanceof Patient) {
            return "PATIENT";
        } else if (user instanceof Doctor) {
            return "DOCTOR";
        } else if (user instanceof Receptionist) {
            return "RECEPTIONIST";
        } else if (user instanceof Pharmacist) {
            return "PHARMACIST";
        }
        return "UNKNOWN";
    }

    /**
     * Change user password
     */
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = authenticate(email, oldPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Reset password (admin function)
     */
    public void resetPassword(String email, String newPassword) {
        User user = getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}