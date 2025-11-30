package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Appointment;
import com.v322.healthsync.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // FR-3: Book Appointment
    @PostMapping
    public ResponseEntity<AppointmentDTO> bookAppointment(@RequestBody Appointment appointment) {
        try {
            Appointment bookedAppointment = appointmentService.bookAppointment(appointment);
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(bookedAppointment), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-4: View Doctor Availability - Get Available Slots
    @GetMapping("/available-slots")
    public ResponseEntity<List<AppointmentService.TimeSlot>> getAvailableSlots(
            @RequestParam String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots(doctorId, date);
            return new ResponseEntity<>(slots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-5: Update Appointment
    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable String appointmentId,
                                                         @RequestBody Appointment appointment) {
        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, appointment);
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(updatedAppointment), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-6: Cancel Appointment
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable String appointmentId) {
        try {
            appointmentService.cancelAppointment(appointmentId);
            return new ResponseEntity<>("Appointment cancelled successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to cancel appointment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-7: View Appointment Details
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(appointment), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable String patientId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(@PathVariable String doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
            List<AppointmentDTO> responseList = appointments.stream().map(DTOMapper::toAppointmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByStatus(@PathVariable String status) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
            List<AppointmentDTO> responseList = appointments.stream().map(DTOMapper::toAppointmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-8: Conduct Consultation
    @PutMapping("/{appointmentId}/consultation")
    public ResponseEntity<AppointmentDTO> conductConsultation(
            @PathVariable String appointmentId,
            @RequestParam String diagnosis,
            @RequestParam String treatmentPlan,
            @RequestParam(required = false) String notes) {
        try {
            Appointment appointment = appointmentService.conductConsultation(
                appointmentId, diagnosis, treatmentPlan, notes);
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(appointment), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.entity.User;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.service.AuthService;
import com.v322.healthsync.service.PatientService;
import com.v322.healthsync.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
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
    public ResponseEntity<?> register(@RequestBody Patient patient) {
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

            // Register patient (password will be hashed in service)
            Patient registeredPatient = authService.registerPatient(patient);

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
}package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Bed;
import com.v322.healthsync.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "*")
public class BedController {

    @Autowired
    private BedService bedService;

    @PostMapping
    public ResponseEntity<BedDTO> createBed(@RequestBody Bed bed) {
        try {
            Bed createdBed = bedService.createBed(bed);
            return new ResponseEntity<>(DTOMapper.toBedDTO(createdBed), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-16: Assign Bed to Patient
    @PostMapping("/{bedId}/assign")
    public ResponseEntity<BedDTO> assignBedToPatient(
            @PathVariable String bedId,
            @RequestParam String patientId) {
        try {
            Bed bed = bedService.assignBedToPatient(bedId, patientId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-16: Release Bed from Patient
    @PostMapping("/{bedId}/release")
    public ResponseEntity<BedDTO> releaseBed(@PathVariable String bedId) {
        try {
            Bed bed = bedService.releaseBed(bedId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/patient/{patientId}/release")
    public ResponseEntity<BedDTO> releaseBedByPatient(@PathVariable String patientId) {
        try {
            Bed bed = bedService.releaseBedByPatient(patientId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-17: Check Bed Availability
    @GetMapping("/available")
    public ResponseEntity<List<BedDTO>> getAvailableBeds() {
        try {
            List<Bed> beds = bedService.getAvailableBeds();
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available/department/{departmentId}")
    public ResponseEntity<List<BedDTO>> getAvailableBedsByDepartment(@PathVariable String departmentId) {
        try {
            List<Bed> beds = bedService.getAvailableBedsByDepartment(departmentId);
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available/count/department/{departmentId}")
    public ResponseEntity<Long> countAvailableBedsByDepartment(@PathVariable String departmentId) {
        try {
            Long count = bedService.countAvailableBedsByDepartment(departmentId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{bedId}")
    public ResponseEntity<BedDTO> getBedById(@PathVariable String bedId) {
        try {
            Bed bed = bedService.getBedById(bedId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<BedDTO>> getAllBeds() {
        try {
            List<Bed> beds = bedService.getAllBeds();
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<BedDTO>> getBedsByDepartment(@PathVariable String departmentId) {
        try {
            List<Bed> beds = bedService.getBedsByDepartment(departmentId);
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/occupied")
    public ResponseEntity<List<BedDTO>> getOccupiedBeds() {
        try {
            List<Bed> beds = bedService.getOccupiedBeds();
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<BedDTO> getBedByPatient(@PathVariable String patientId) {
        try {
            Bed bed = bedService.getBedByPatient(patientId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{bedId}")
    public ResponseEntity<BedDTO> updateBed(@PathVariable String bedId, @RequestBody Bed bed) {
        try {
            Bed updatedBed = bedService.updateBed(bedId, bed);
            return new ResponseEntity<>(DTOMapper.toBedDTO(updatedBed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{bedId}")
    public ResponseEntity<String> deleteBed(@PathVariable String bedId) {
        try {
            bedService.deleteBed(bedId);
            return new ResponseEntity<>("Bed deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete bed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Bill;
import com.v322.healthsync.entity.BillItem;
import com.v322.healthsync.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillingController {

    @Autowired
    private BillingService billingService;

    // FR-18: Generate Bill
    @PostMapping
    public ResponseEntity<BillDTO> generateBill(@RequestBody Bill bill) {
        try {
            Bill generatedBill = billingService.generateBill(bill);
            return new ResponseEntity<>(DTOMapper.toBillDTO(generatedBill), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/with-items")
    public ResponseEntity<BillDTO> generateBillWithItems(@RequestBody BillRequest request) {
        try {
            Bill bill = billingService.generateBillWithItems(request.getBill(), request.getItems());
            return new ResponseEntity<>(DTOMapper.toBillDTO(bill), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-19: Add Bill Items
    @PostMapping("/items")
    public ResponseEntity<BillItemDTO> addBillItem(@RequestBody BillItem item) {
        try {
            BillItem createdItem = billingService.addBillItem(item);
            return new ResponseEntity<>(DTOMapper.toBillItemDTO(createdItem), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-20: View Bill Details
    @GetMapping("/{billId}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable String billId) {
        try {
            Bill bill = billingService.getBillById(billId);
            return new ResponseEntity<>(DTOMapper.toBillDTO(bill), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillDTO>> getBillsByPatient(@PathVariable String patientId) {
        try {
            List<Bill> bills = billingService.getBillsByPatient(patientId);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillDTO>> getBillsByStatus(@PathVariable String status) {
        try {
            List<Bill> bills = billingService.getBillsByStatus(status);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{billId}/items")
    public ResponseEntity<List<BillItemDTO>> getBillItems(@PathVariable String billId) {
        try {
            List<BillItem> items = billingService.getBillItems(billId);
            List<BillItemDTO> responseList = items.stream().map(DTOMapper::toBillItemDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unpaid")
    public ResponseEntity<List<BillDTO>> getUnpaidBills() {
        try {
            List<Bill> bills = billingService.getUnpaidBills();
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unpaid/patient/{patientId}")
    public ResponseEntity<List<BillDTO>> getUnpaidBillsByPatient(@PathVariable String patientId) {
        try {
            List<Bill> bills = billingService.getUnpaidBillsByPatient(patientId);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total/patient/{patientId}")
    public ResponseEntity<BigDecimal> getTotalAmountByPatient(@PathVariable String patientId) {
        try {
            BigDecimal total = billingService.getTotalAmountByPatient(patientId);
            return new ResponseEntity<>(total, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BillDTO>> getBillsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Bill> bills = billingService.getBillsByDateRange(startDate, endDate);
            List<BillDTO> responseList = bills.stream().map(DTOMapper::toBillDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Process Payment
    @PostMapping("/{billId}/payment")
    public ResponseEntity<BillDTO> processPayment(
            @PathVariable String billId,
            @RequestParam BigDecimal amount) {
        try {
            Bill bill = billingService.processPayment(billId, amount);
            return new ResponseEntity<>(DTOMapper.toBillDTO(bill), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{billId}")
    public ResponseEntity<BillDTO> updateBill(@PathVariable String billId, @RequestBody Bill bill) {
        try {
            Bill updatedBill = billingService.updateBill(billId, bill);
            return new ResponseEntity<>(DTOMapper.toBillDTO(updatedBill), HttpStatus.OK);   
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<BillItemDTO> updateBillItem(
            @PathVariable String itemId,
            @RequestBody BillItem item) {
        try {
            BillItem updatedItem = billingService.updateBillItem(itemId, item);
            return new ResponseEntity<>(DTOMapper.toBillItemDTO(updatedItem), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{billId}")
    public ResponseEntity<String> deleteBill(@PathVariable String billId) {
        try {
            billingService.deleteBill(billId);
            return new ResponseEntity<>("Bill deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete bill", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deleteBillItem(@PathVariable String itemId) {
        try {
            billingService.deleteBillItem(itemId);
            return new ResponseEntity<>("Bill item deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete bill item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper class for request body
    public static class BillRequest {
        private Bill bill;
        private List<BillItem> items;

        public Bill getBill() { return bill; }
        public void setBill(Bill bill) { this.bill = bill; }
        public List<BillItem> getItems() { return items; }
        public void setItems(List<BillItem> items) { this.items = items; }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.entity.Department;
import com.v322.healthsync.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        try {
            Department createdDepartment = departmentService.createDepartment(department);
            return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable String departmentId) {
        try {
            Department department = departmentService.getDepartmentById(departmentId);
            return new ResponseEntity<>(department, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Department> getDepartmentByName(@PathVariable String name) {
        try {
            Department department = departmentService.getDepartmentByName(name);
            return new ResponseEntity<>(department, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return new ResponseEntity<>(departments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Department>> getDepartmentsByLocation(@PathVariable String location) {
        try {
            List<Department> departments = departmentService.getDepartmentsByLocation(location);
            return new ResponseEntity<>(departments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Department>> searchDepartmentsByName(@RequestParam String keyword) {
        try {
            List<Department> departments = departmentService.searchDepartmentsByName(keyword);
            return new ResponseEntity<>(departments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable String departmentId,
            @RequestBody Department department) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(departmentId, department);
            return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<String> deleteDepartment(@PathVariable String departmentId) {
        try {
            departmentService.deleteDepartment(departmentId);
            return new ResponseEntity<>("Department deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete department", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Doctor;
import com.v322.healthsync.entity.DoctorAvailability;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody Doctor doctor) {
        try {
            Doctor createdDoctor = doctorService.createDoctor(doctor);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(createdDoctor), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable String doctorId) {
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(doctor), HttpStatus.OK);  
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<DoctorDTO> getDoctorByEmail(@PathVariable String email) {
        try {
            Doctor doctor = doctorService.getDoctorByEmail(email);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(doctor), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByDepartment(@PathVariable String departmentId) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByDepartment(departmentId);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@PathVariable String specialization) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctorsByName(@RequestParam String name) {
        try {
            List<Doctor> doctors = doctorService.searchDoctorsByName(name);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fee")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByConsultationFee(@RequestParam BigDecimal maxFee) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByConsultationFee(maxFee);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-9: Manage Doctor Availability
    @PostMapping("/{doctorId}/availability")
    public ResponseEntity<DoctorAvailabilityDTO> addDoctorAvailability(
            @PathVariable String doctorId,
            @RequestBody DoctorAvailability availability) {
        try {
            DoctorAvailability createdAvailability = doctorService.addDoctorAvailability(availability);
            return new ResponseEntity<>(DTOMapper.toDoctorAvailabilityDTO(createdAvailability), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/availability/{slotId}")
    public ResponseEntity<DoctorAvailabilityDTO> updateDoctorAvailability(
            @PathVariable String slotId,
            @RequestBody DoctorAvailability availability) {
        try {
            DoctorAvailability updatedAvailability = doctorService.updateDoctorAvailability(slotId, availability);
            return new ResponseEntity<>(DTOMapper.toDoctorAvailabilityDTO(updatedAvailability), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/availability/{slotId}")
    public ResponseEntity<String> deleteDoctorAvailability(@PathVariable String slotId) {
        try {
            doctorService.deleteDoctorAvailability(slotId);
            return new ResponseEntity<>("Availability slot deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete availability slot", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getDoctorAvailability(@PathVariable String doctorId) {
        try {
            List<DoctorAvailability> availabilities = doctorService.getDoctorAvailability(doctorId);
            List<DoctorAvailabilityDTO> responseList = availabilities.stream().map(DTOMapper::toDoctorAvailabilityDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{doctorId}/availability/{dayOfWeek}")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getDoctorAvailabilityByDay(
            @PathVariable String doctorId,
            @PathVariable String dayOfWeek) {
        try {
            List<DoctorAvailability> availabilities = doctorService.getDoctorAvailabilityByDay(doctorId, dayOfWeek);
            List<DoctorAvailabilityDTO> responseList = availabilities.stream().map(DTOMapper::toDoctorAvailabilityDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-10: View Patient List
    @GetMapping("/{doctorId}/patients")
    public ResponseEntity<List<PatientDTO>> getPatientListForDoctor(@PathVariable String doctorId) {
        try {
            List<Patient> patients = doctorService.getPatientListForDoctor(doctorId);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-11: Update Consultation Fee
    @PutMapping("/{doctorId}/consultation-fee")
    public ResponseEntity<DoctorDTO> updateConsultationFee(
            @PathVariable String doctorId,
            @RequestParam BigDecimal newFee) {
        try {
            Doctor doctor = doctorService.updateConsultationFee(doctorId, newFee);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(doctor), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable String doctorId, @RequestBody Doctor doctor) {
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(doctorId, doctor);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(updatedDoctor), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<String> deleteDoctor(@PathVariable String doctorId) {
        try {
            doctorService.deleteDoctor(doctorId);
            return new ResponseEntity<>("Doctor deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete doctor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}package com.v322.healthsync.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HTMLController {

    // @GetMapping("")
    // public ModelAndView home() {
    //     ModelAndView mav=new ModelAndView("index");
    //     return mav;
    // }
    // @RequestMapping("/error")
    // public String error() {
    //     return "forward:/404.html";
    // }
    @RequestMapping("/")
    public String home() {
        System.err.println("test");
        return "forward:/index.html"; // Thymeleaf resolves this to src/main/resources/templates/index.html
    }

}package com.v322.healthsync.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IndexController {

    // @GetMapping("")
    // public ModelAndView home() {
    //     ModelAndView mav=new ModelAndView("index");
    //     return mav;
    // }
    

    @GetMapping("/ping")
    public String ping() {
        return "PINGED Server";
    }
    
    @GetMapping("/api/**")
    public String _home() {
        return "API";
    }

}package com.v322.healthsync.controller;

import com.v322.healthsync.entity.Medication;
import com.v322.healthsync.entity.Pharmacy;
import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.service.MedicationService;
import com.v322.healthsync.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
@CrossOrigin(origins = "*")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    // FR-14: Manage Medication Inventory - Add
    @PostMapping
    public ResponseEntity<Medication> addMedication(@RequestBody Medication medication) {
        try {
            Medication createdMedication = medicationService.addMedication(medication);
            return new ResponseEntity<>(createdMedication, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-14: Manage Medication Inventory - Update
    @PutMapping("/{medicationId}")
    public ResponseEntity<Medication> updateMedication(
            @PathVariable String medicationId,
            @RequestBody Medication medication) {
        try {
            Medication updatedMedication = medicationService.updateMedication(medicationId, medication);
            return new ResponseEntity<>(updatedMedication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-14: Manage Medication Inventory - Remove
    @DeleteMapping("/{medicationId}")
    public ResponseEntity<String> deleteMedication(@PathVariable String medicationId) {
        try {
            medicationService.deleteMedication(medicationId);
            return new ResponseEntity<>("Medication deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete medication", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-15: Check Medication Availability
    @GetMapping("/{medicationId}")
    public ResponseEntity<Medication> getMedicationById(@PathVariable String medicationId) {
        try {
            Medication medication = medicationService.getMedicationById(medicationId);
            return new ResponseEntity<>(medication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Medication> getMedicationByName(@PathVariable String name) {
        try {
            Medication medication = medicationService.getMedicationByName(name);
            return new ResponseEntity<>(medication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Medication>> getAllMedications() {
        try {
            List<Medication> medications = medicationService.getAllMedications();
            return new ResponseEntity<>(medications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<List<Medication>> getMedicationsByPharmacy(@PathVariable String pharmacyId) {
        try {
            List<Medication> medications = medicationService.getMedicationsByPharmacy(pharmacyId);
            return new ResponseEntity<>(medications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Medication>> searchMedicationsByKeyword(@RequestParam String keyword) {
        try {
            List<Medication> medications = medicationService.searchMedicationsByKeyword(keyword);
            return new ResponseEntity<>(medications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<Medication>> getMedicationsByManufacturer(@PathVariable String manufacturer) {
        try {
            List<Medication> medications = medicationService.getMedicationsByManufacturer(manufacturer);
            return new ResponseEntity<>(medications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Medication>> getMedicationsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        try {
            List<Medication> medications = medicationService.getMedicationsByPriceRange(minPrice, maxPrice);
            return new ResponseEntity<>(medications, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-13: Dispense Medication
    @PostMapping("/dispense/{prescriptionId}")
    public ResponseEntity<Prescription> dispenseMedication(@PathVariable String prescriptionId) {
        try {
            Prescription prescription = medicationService.dispenseMedication(prescriptionId);
            return new ResponseEntity<>(prescription, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // FR-1: Patient Registration
    @PostMapping("/register")
    public ResponseEntity<PatientDTO> registerPatient(@RequestBody Patient patient) {
        try {
            Patient registeredPatient = patientService.registerPatient(patient);
            return new ResponseEntity<>(DTOMapper.toPatientDTO(registeredPatient), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-1: Update Patient Information
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable String patientId, 
                                                 @RequestBody Patient patient) {
        try {
            Patient updatedPatient = patientService.updatePatient(patientId, patient);
            return new ResponseEntity<>(DTOMapper.toPatientDTO(updatedPatient), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String patientId) {
        try {
            Patient patient = patientService.getPatientById(patientId);
            return new ResponseEntity<>(DTOMapper.toPatientDTO(patient), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PatientDTO> getPatientByEmail(@PathVariable String email) {
        try {
            Patient patient = patientService.getPatientByEmail(email);
            return new ResponseEntity<>(DTOMapper.toPatientDTO(patient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatientsByName(@RequestParam String name) {
        try {
            List<Patient> patients = patientService.searchPatientsByName(name);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<PatientDTO>> getPatientsByCity(@PathVariable String city) {
        try {
            List<Patient> patients = patientService.getPatientsByCity(city);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bloodgroup/{bloodGroup}")
    public ResponseEntity<List<PatientDTO>> getPatientsByBloodGroup(@PathVariable String bloodGroup) {
        try {
            List<Patient> patients = patientService.getPatientsByBloodGroup(bloodGroup);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-2: View Medical History
    @GetMapping("/{patientId}/medical-history")
    public ResponseEntity<PatientService.MedicalHistory> getMedicalHistory(@PathVariable String patientId) {
        try {
            PatientService.MedicalHistory history = patientService.getMedicalHistory(patientId);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<String> deletePatient(@PathVariable String patientId) {
        try {
            patientService.deletePatient(patientId);
            return new ResponseEntity<>("Patient deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete patient", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.service.ReceptionistService;
import com.v322.healthsync.service.PharmacistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacists")
@CrossOrigin(origins = "*")
class PharmacistController {

    @Autowired
    private PharmacistService pharmacistService;

    @PostMapping
    public ResponseEntity<Pharmacist> createPharmacist(@RequestBody Pharmacist pharmacist) {
        try {
            Pharmacist createdPharmacist = pharmacistService.createPharmacist(pharmacist);
            return new ResponseEntity<>(createdPharmacist, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{pharmacistId}")
    public ResponseEntity<Pharmacist> getPharmacistById(@PathVariable String pharmacistId) {
        try {
            Pharmacist pharmacist = pharmacistService.getPharmacistById(pharmacistId);
            return new ResponseEntity<>(pharmacist, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Pharmacist> getPharmacistByEmail(@PathVariable String email) {
        try {
            Pharmacist pharmacist = pharmacistService.getPharmacistByEmail(email);
            return new ResponseEntity<>(pharmacist, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<Pharmacist> getPharmacistByPharmacy(@PathVariable String pharmacyId) {
        try {
            Pharmacist pharmacist = pharmacistService.getPharmacistByPharmacy(pharmacyId);
            return new ResponseEntity<>(pharmacist, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Pharmacist>> getAllPharmacists() {
        try {
            List<Pharmacist> pharmacists = pharmacistService.getAllPharmacists();
            return new ResponseEntity<>(pharmacists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{pharmacistId}")
    public ResponseEntity<Pharmacist> updatePharmacist(
            @PathVariable String pharmacistId,
            @RequestBody Pharmacist pharmacist) {
        try {
            Pharmacist updatedPharmacist = pharmacistService.updatePharmacist(pharmacistId, pharmacist);
            return new ResponseEntity<>(updatedPharmacist, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{pharmacistId}")
    public ResponseEntity<String> deletePharmacist(@PathVariable String pharmacistId) {
        try {
            pharmacistService.deletePharmacist(pharmacistId);
            return new ResponseEntity<>("Pharmacist deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete pharmacist", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.entity.Medication;
import com.v322.healthsync.entity.Pharmacy;
import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.service.MedicationService;
import com.v322.healthsync.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@CrossOrigin(origins = "*")
class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @PostMapping
    public ResponseEntity<Pharmacy> createPharmacy(@RequestBody Pharmacy pharmacy) {
        try {
            Pharmacy createdPharmacy = pharmacyService.createPharmacy(pharmacy);
            return new ResponseEntity<>(createdPharmacy, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{pharmacyId}")
    public ResponseEntity<Pharmacy> getPharmacyById(@PathVariable String pharmacyId) {
        try {
            Pharmacy pharmacy = pharmacyService.getPharmacyById(pharmacyId);
            return new ResponseEntity<>(pharmacy, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Pharmacy>> getAllPharmacies() {
        try {
            List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
            return new ResponseEntity<>(pharmacies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Pharmacy>> getPharmaciesByLocation(@PathVariable String location) {
        try {
            List<Pharmacy> pharmacies = pharmacyService.getPharmaciesByLocation(location);
            return new ResponseEntity<>(pharmacies, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{pharmacyId}")
    public ResponseEntity<Pharmacy> updatePharmacy(
            @PathVariable String pharmacyId,
            @RequestBody Pharmacy pharmacy) {
        try {
            Pharmacy updatedPharmacy = pharmacyService.updatePharmacy(pharmacyId, pharmacy);
            return new ResponseEntity<>(updatedPharmacy, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{pharmacyId}")
    public ResponseEntity<String> deletePharmacy(@PathVariable String pharmacyId) {
        try {
            pharmacyService.deletePharmacy(pharmacyId);
            return new ResponseEntity<>("Pharmacy deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete pharmacy", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.entity.PrescriptionItem;
import com.v322.healthsync.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // FR-12: Write Prescription
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        try {
            Prescription createdPrescription = prescriptionService.createPrescription(prescription);
            return new ResponseEntity<>(createdPrescription, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/with-items")
    public ResponseEntity<Prescription> createPrescriptionWithItems(
            @RequestBody PrescriptionRequest request) {
        try {
            Prescription prescription = prescriptionService.createPrescriptionWithItems(
                request.getPrescription(), request.getItems());
            return new ResponseEntity<>(prescription, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<PrescriptionItem> addPrescriptionItem(@RequestBody PrescriptionItem item) {
        try {
            PrescriptionItem createdItem = prescriptionService.addPrescriptionItem(item);
            return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-12: View Prescription
    @GetMapping("/{prescriptionId}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable String prescriptionId) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
            return new ResponseEntity<>(prescription, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByPatient(@PathVariable String patientId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
            return new ResponseEntity<>(prescriptions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDoctor(@PathVariable String doctorId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
            return new ResponseEntity<>(prescriptions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByStatus(@PathVariable String status) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByStatus(status);
            return new ResponseEntity<>(prescriptions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{prescriptionId}/items")
    public ResponseEntity<List<PrescriptionItem>> getPrescriptionItems(@PathVariable String prescriptionId) {
        try {
            List<PrescriptionItem> items = prescriptionService.getPrescriptionItems(prescriptionId);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDateRange(startDate, endDate);
            return new ResponseEntity<>(prescriptions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-13: Update Prescription Status (for dispensing)
    @PutMapping("/{prescriptionId}/status")
    public ResponseEntity<Prescription> updatePrescriptionStatus(
            @PathVariable String prescriptionId,
            @RequestParam String status) {
        try {
            Prescription prescription = prescriptionService.updatePrescriptionStatus(prescriptionId, status);
            return new ResponseEntity<>(prescription, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{prescriptionId}")
    public ResponseEntity<Prescription> updatePrescription(
            @PathVariable String prescriptionId,
            @RequestBody Prescription prescription) {
        try {
            Prescription updatedPrescription = prescriptionService.updatePrescription(prescriptionId, prescription);
            return new ResponseEntity<>(updatedPrescription, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity<String> deletePrescription(@PathVariable String prescriptionId) {
        try {
            prescriptionService.deletePrescription(prescriptionId);
            return new ResponseEntity<>("Prescription deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete prescription", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deletePrescriptionItem(@PathVariable String itemId) {
        try {
            prescriptionService.deletePrescriptionItem(itemId);
            return new ResponseEntity<>("Prescription item deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete prescription item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper class for request body
    public static class PrescriptionRequest {
        private Prescription prescription;
        private List<PrescriptionItem> items;

        public Prescription getPrescription() { return prescription; }
        public void setPrescription(Prescription prescription) { this.prescription = prescription; }
        public List<PrescriptionItem> getItems() { return items; }
        public void setItems(List<PrescriptionItem> items) { this.items = items; }
    }
}package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.service.ReceptionistService;
import com.v322.healthsync.service.PharmacistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/receptionists")
@CrossOrigin(origins = "*")
public class ReceptionistController {

    @Autowired
    private ReceptionistService receptionistService;

    @PostMapping
    public ResponseEntity<ReceptionistDTO> createReceptionist(@RequestBody Receptionist receptionist) {
        try {
            Receptionist createdReceptionist = receptionistService.createReceptionist(receptionist);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(createdReceptionist), HttpStatus.CREATED);  
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{receptionistId}")
    public ResponseEntity<ReceptionistDTO> getReceptionistById(@PathVariable String receptionistId) {
        try {
            Receptionist receptionist = receptionistService.getReceptionistById(receptionistId);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(receptionist), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ReceptionistDTO> getReceptionistByEmail(@PathVariable String email) {
        try {
            Receptionist receptionist = receptionistService.getReceptionistByEmail(email);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(receptionist), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/contact/{contactNumber}")
    public ResponseEntity<ReceptionistDTO> getReceptionistByContactNumber(@PathVariable String contactNumber) {
        try {
            Receptionist receptionist = receptionistService.getReceptionistByContactNumber(contactNumber);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(receptionist), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<ReceptionistDTO>> getAllReceptionists() {
        try {
            List<Receptionist> receptionists = receptionistService.getAllReceptionists();
            List<ReceptionistDTO> responseList = receptionists.stream().map(DTOMapper::toReceptionistDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{receptionistId}")
    public ResponseEntity<ReceptionistDTO> updateReceptionist(
            @PathVariable String receptionistId,
            @RequestBody Receptionist receptionist) {
        try {
            Receptionist updatedReceptionist = receptionistService.updateReceptionist(receptionistId, receptionist);
            return new ResponseEntity<>(DTOMapper.toReceptionistDTO(updatedReceptionist), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{receptionistId}")
    public ResponseEntity<String> deleteReceptionist(@PathVariable String receptionistId) {
        try {
            receptionistService.deleteReceptionist(receptionistId);
            return new ResponseEntity<>("Receptionist deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete receptionist", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
package com.v322.healthsync.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/api/test")
public class TestController {

    // @GetMapping("")
    // public ModelAndView home() {
    //     ModelAndView mav=new ModelAndView("index");
    //     return mav;
    // }
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ping Successful");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}