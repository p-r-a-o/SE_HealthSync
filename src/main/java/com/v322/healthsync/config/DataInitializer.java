package com.v322.healthsync.config;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ReceptionistRepository receptionistRepository;

    @Autowired
    private PharmacistRepository pharmacistRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PrescriptionItemRepository prescriptionItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (departmentRepository.count() > 0) {
            System.out.println("Database already initialized. Skipping data initialization.");
            return;
        }

        System.out.println("Initializing database with sample data...");

        // 1. Create Departments
        List<Department> departments = createDepartments();

        // 2. Create Doctors
        List<Doctor> doctors = createDoctors(departments);

        // 3. Create Doctor Availabilities
        createDoctorAvailabilities(doctors);

        // 4. Create Patients
        List<Patient> patients = createPatients();

        // 5. Create Receptionists
        createReceptionists();

        // 6. Create Pharmacy and Pharmacist
        Pharmacy pharmacy = createPharmacy();
        createPharmacist(pharmacy);

        // 7. Create Medications
        List<Medication> medications = createMedications(pharmacy);

        // 8. Create Beds
        createBeds(departments);

        // 9. Create Appointments
        List<Appointment> appointments = createAppointments(patients, doctors);

        // 10. Create Prescriptions
        createPrescriptions(patients, doctors, appointments, medications);

        System.out.println("✅ Database initialization completed successfully!");
        System.out.println("\n=== LOGIN CREDENTIALS ===");
        System.out.println("\nDoctors:");
        System.out.println("  dr.smith@hospital.com / password123");
        System.out.println("  dr.johnson@hospital.com / password123");
        System.out.println("  dr.williams@hospital.com / password123");
        System.out.println("  dr.brown@hospital.com / password123");
        System.out.println("\nPatients:");
        System.out.println("  john.doe@email.com / password123");
        System.out.println("  jane.smith@email.com / password123");
        System.out.println("  mike.wilson@email.com / password123");
        System.out.println("  sarah.davis@email.com / password123");
        System.out.println("\nReceptionists:");
        System.out.println("  emma.white@hospital.com / password123");
        System.out.println("  olivia.green@hospital.com / password123");
        System.out.println("\nPharmacist:");
        System.out.println("  robert.miller@hospital.com / password123");
        System.out.println("========================\n");
    }

    private List<Department> createDepartments() {
        List<Department> departments = new ArrayList<>();

        Department cardiology = Department.builder()
            .departmentId("DEPT-001")
            .name("Cardiology")
            .location("Building A, Floor 3")
            .build();

        Department neurology = Department.builder()
            .departmentId("DEPT-002")
            .name("Neurology")
            .location("Building B, Floor 2")
            .build();

        Department orthopedics = Department.builder()
            .departmentId("DEPT-003")
            .name("Orthopedics")
            .location("Building A, Floor 1")
            .build();

        Department pediatrics = Department.builder()
            .departmentId("DEPT-004")
            .name("Pediatrics")
            .location("Building C, Floor 1")
            .build();

        departments.add(departmentRepository.save(cardiology));
        departments.add(departmentRepository.save(neurology));
        departments.add(departmentRepository.save(orthopedics));
        departments.add(departmentRepository.save(pediatrics));

        System.out.println("✓ Created " + departments.size() + " departments");
        return departments;
    }

    private List<Doctor> createDoctors(List<Department> departments) {
        List<Doctor> doctors = new ArrayList<>();

        Doctor doctor1 = new Doctor();
        doctor1.setPersonId("DOC-001");
        doctor1.setFirstName("John");
        doctor1.setLastName("Smith");
        doctor1.setEmail("dr.smith@hospital.com");
        doctor1.setPassword(passwordEncoder.encode("password123"));
        doctor1.setDateOfBirth(LocalDate.of(1975, 3, 15));
        doctor1.setGender("Male");
        doctor1.setBloodGroup("O+");
        doctor1.setContactNumber("9876543210");
        doctor1.setCity("New York");
        doctor1.setSpecialization("Cardiology");
        doctor1.setQualification("MD, DM (Cardiology), MBBS");
        doctor1.setConsultationFee(new BigDecimal("500.00"));
        doctor1.setDepartment(departments.get(0));

        Doctor doctor2 = new Doctor();
        doctor2.setPersonId("DOC-002");
        doctor2.setFirstName("Emily");
        doctor2.setLastName("Johnson");
        doctor2.setEmail("dr.johnson@hospital.com");
        doctor2.setPassword(passwordEncoder.encode("password123"));
        doctor2.setDateOfBirth(LocalDate.of(1980, 7, 22));
        doctor2.setGender("Female");
        doctor2.setBloodGroup("A+");
        doctor2.setContactNumber("9876543211");
        doctor2.setCity("Los Angeles");
        doctor2.setSpecialization("Neurology");
        doctor2.setQualification("MD, DM (Neurology), MBBS");
        doctor2.setConsultationFee(new BigDecimal("600.00"));
        doctor2.setDepartment(departments.get(1));

        Doctor doctor3 = new Doctor();
        doctor3.setPersonId("DOC-003");
        doctor3.setFirstName("Michael");
        doctor3.setLastName("Williams");
        doctor3.setEmail("dr.williams@hospital.com");
        doctor3.setPassword(passwordEncoder.encode("password123"));
        doctor3.setDateOfBirth(LocalDate.of(1978, 11, 8));
        doctor3.setGender("Male");
        doctor3.setBloodGroup("B+");
        doctor3.setContactNumber("9876543212");
        doctor3.setCity("Chicago");
        doctor3.setSpecialization("Orthopedics");
        doctor3.setQualification("MS (Ortho), MBBS");
        doctor3.setConsultationFee(new BigDecimal("450.00"));
        doctor3.setDepartment(departments.get(2));

        Doctor doctor4 = new Doctor();
        doctor4.setPersonId("DOC-004");
        doctor4.setFirstName("Sarah");
        doctor4.setLastName("Brown");
        doctor4.setEmail("dr.brown@hospital.com");
        doctor4.setPassword(passwordEncoder.encode("password123"));
        doctor4.setDateOfBirth(LocalDate.of(1982, 5, 19));
        doctor4.setGender("Female");
        doctor4.setBloodGroup("AB+");
        doctor4.setContactNumber("9876543213");
        doctor4.setCity("Houston");
        doctor4.setSpecialization("Pediatrics");
        doctor4.setQualification("MD (Pediatrics), MBBS");
        doctor4.setConsultationFee(new BigDecimal("400.00"));
        doctor4.setDepartment(departments.get(3));

        doctors.add(doctorRepository.save(doctor1));
        doctors.add(doctorRepository.save(doctor2));
        doctors.add(doctorRepository.save(doctor3));
        doctors.add(doctorRepository.save(doctor4));

        System.out.println("✓ Created " + doctors.size() + " doctors");
        return doctors;
    }

    private void createDoctorAvailabilities(List<Doctor> doctors) {
        int count = 0;
        String[] weekdays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

        for (Doctor doctor : doctors) {
            for (String day : weekdays) {
                // Morning slot: 9:00 AM - 1:00 PM
                DoctorAvailability morningSlot = DoctorAvailability.builder()
                    .slotId("SLOT-" + (++count))
                    .doctor(doctor)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(13, 0))
                    .build();
                doctorAvailabilityRepository.save(morningSlot);

                // Evening slot: 3:00 PM - 7:00 PM
                DoctorAvailability eveningSlot = DoctorAvailability.builder()
                    .slotId("SLOT-" + (++count))
                    .doctor(doctor)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(15, 0))
                    .endTime(LocalTime.of(19, 0))
                    .build();
                doctorAvailabilityRepository.save(eveningSlot);
            }
        }

        System.out.println("✓ Created " + count + " doctor availability slots");
    }

    private List<Patient> createPatients() {
        List<Patient> patients = new ArrayList<>();

        Patient patient1 = new Patient();
        patient1.setPersonId("PAT-001");
        patient1.setFirstName("John");
        patient1.setLastName("Doe");
        patient1.setEmail("john.doe@email.com");
        patient1.setPassword(passwordEncoder.encode("password123"));
        patient1.setDateOfBirth(LocalDate.of(1990, 6, 15));
        patient1.setGender("Male");
        patient1.setBloodGroup("O+");
        patient1.setContactNumber("1234567890");
        patient1.setCity("New York");
        patient1.setRegistrationDate(LocalDate.now().minusMonths(6));
        patient1.setNotes("No known allergies");

        Patient patient2 = new Patient();
        patient2.setPersonId("PAT-002");
        patient2.setFirstName("Jane");
        patient2.setLastName("Smith");
        patient2.setEmail("jane.smith@email.com");
        patient2.setPassword(passwordEncoder.encode("password123"));
        patient2.setDateOfBirth(LocalDate.of(1985, 9, 22));
        patient2.setGender("Female");
        patient2.setBloodGroup("A+");
        patient2.setContactNumber("1234567891");
        patient2.setCity("Los Angeles");
        patient2.setRegistrationDate(LocalDate.now().minusMonths(4));
        patient2.setNotes("Allergic to penicillin");

        Patient patient3 = new Patient();
        patient3.setPersonId("PAT-003");
        patient3.setFirstName("Mike");
        patient3.setLastName("Wilson");
        patient3.setEmail("mike.wilson@email.com");
        patient3.setPassword(passwordEncoder.encode("password123"));
        patient3.setDateOfBirth(LocalDate.of(1995, 12, 10));
        patient3.setGender("Male");
        patient3.setBloodGroup("B+");
        patient3.setContactNumber("1234567892");
        patient3.setCity("Chicago");
        patient3.setRegistrationDate(LocalDate.now().minusMonths(2));
        patient3.setNotes("Diabetic patient");

        Patient patient4 = new Patient();
        patient4.setPersonId("PAT-004");
        patient4.setFirstName("Sarah");
        patient4.setLastName("Davis");
        patient4.setEmail("sarah.davis@email.com");
        patient4.setPassword(passwordEncoder.encode("password123"));
        patient4.setDateOfBirth(LocalDate.of(2010, 4, 5));
        patient4.setGender("Female");
        patient4.setBloodGroup("AB+");
        patient4.setContactNumber("1234567893");
        patient4.setCity("Houston");
        patient4.setRegistrationDate(LocalDate.now().minusMonths(1));
        patient4.setNotes("Asthma patient");

        patients.add(patientRepository.save(patient1));
        patients.add(patientRepository.save(patient2));
        patients.add(patientRepository.save(patient3));
        patients.add(patientRepository.save(patient4));

        System.out.println("✓ Created " + patients.size() + " patients");
        return patients;
    }

    private void createReceptionists() {
        Receptionist receptionist1 = new Receptionist();
        receptionist1.setPersonId("REC-001");
        receptionist1.setFirstName("Emma");
        receptionist1.setLastName("White");
        receptionist1.setEmail("emma.white@hospital.com");
        receptionist1.setPassword(passwordEncoder.encode("password123"));
        receptionist1.setDateOfBirth(LocalDate.of(1992, 8, 12));
        receptionist1.setGender("Female");
        receptionist1.setBloodGroup("O+");
        receptionist1.setContactNumber("2345678901");
        receptionist1.setCity("New York");

        Receptionist receptionist2 = new Receptionist();
        receptionist2.setPersonId("REC-002");
        receptionist2.setFirstName("Olivia");
        receptionist2.setLastName("Green");
        receptionist2.setEmail("olivia.green@hospital.com");
        receptionist2.setPassword(passwordEncoder.encode("password123"));
        receptionist2.setDateOfBirth(LocalDate.of(1994, 3, 25));
        receptionist2.setGender("Female");
        receptionist2.setBloodGroup("A+");
        receptionist2.setContactNumber("2345678902");
        receptionist2.setCity("Los Angeles");

        receptionistRepository.save(receptionist1);
        receptionistRepository.save(receptionist2);

        System.out.println("✓ Created 2 receptionists");
    }

    private Pharmacy createPharmacy() {
        Pharmacy pharmacy = Pharmacy.builder()
            .pharmacyId("PHAR-001")
            .location("Building D, Ground Floor")
            .build();

        pharmacy = pharmacyRepository.save(pharmacy);
        System.out.println("✓ Created 1 pharmacy");
        return pharmacy;
    }

    private void createPharmacist(Pharmacy pharmacy) {
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setPersonId("PHARM-001");
        pharmacist.setFirstName("Robert");
        pharmacist.setLastName("Miller");
        pharmacist.setEmail("robert.miller@hospital.com");
        pharmacist.setPassword(passwordEncoder.encode("password123"));
        pharmacist.setDateOfBirth(LocalDate.of(1988, 11, 30));
        pharmacist.setGender("Male");
        pharmacist.setBloodGroup("B+");
        pharmacist.setContactNumber("3456789012");
        pharmacist.setCity("New York");
        pharmacist.setPharmacy(pharmacy);

        pharmacistRepository.save(pharmacist);
        System.out.println("✓ Created 1 pharmacist");
    }

    private List<Medication> createMedications(Pharmacy pharmacy) {
        List<Medication> medications = new ArrayList<>();

        Medication[] meds = {
            Medication.builder()
                .medicationId("MED-001")
                .name("Aspirin")
                .genericName("Acetylsalicylic Acid")
                .manufacturer("PharmaCorp")
                .description("Pain reliever and blood thinner")
                .unitPrice(new BigDecimal("5.50"))
                .pharmacy(pharmacy)
                .build(),
            
            Medication.builder()
                .medicationId("MED-002")
                .name("Lisinopril")
                .genericName("Lisinopril")
                .manufacturer("MedLife")
                .description("ACE inhibitor for blood pressure")
                .unitPrice(new BigDecimal("12.00"))
                .pharmacy(pharmacy)
                .build(),
            
            Medication.builder()
                .medicationId("MED-003")
                .name("Metformin")
                .genericName("Metformin Hydrochloride")
                .manufacturer("DiabetesCare")
                .description("Diabetes medication")
                .unitPrice(new BigDecimal("8.75"))
                .pharmacy(pharmacy)
                .build(),
            
            Medication.builder()
                .medicationId("MED-004")
                .name("Amoxicillin")
                .genericName("Amoxicillin")
                .manufacturer("AntibioTech")
                .description("Antibiotic")
                .unitPrice(new BigDecimal("15.00"))
                .pharmacy(pharmacy)
                .build(),
            
            Medication.builder()
                .medicationId("MED-005")
                .name("Albuterol")
                .genericName("Albuterol Sulfate")
                .manufacturer("RespiCare")
                .description("Asthma inhaler")
                .unitPrice(new BigDecimal("25.00"))
                .pharmacy(pharmacy)
                .build(),
            
            Medication.builder()
                .medicationId("MED-006")
                .name("Paracetamol")
                .genericName("Acetaminophen")
                .manufacturer("PainRelief Inc")
                .description("Pain and fever reducer")
                .unitPrice(new BigDecimal("3.50"))
                .pharmacy(pharmacy)
                .build()
        };

        for (Medication med : meds) {
            medications.add(medicationRepository.save(med));
        }

        System.out.println("✓ Created " + medications.size() + " medications");
        return medications;
    }

    private void createBeds(List<Department> departments) {
        int count = 0;
        
        for (int i = 0; i < departments.size(); i++) {
            Department dept = departments.get(i);
            int bedsPerDept = 5;
            
            for (int j = 1; j <= bedsPerDept; j++) {
                Bed bed = Bed.builder()
                    .bedId("BED-" + String.format("%03d", ++count))
                    .department(dept)
                    .isOccupied(j <= 2) // First 2 beds occupied
                    .dailyRate(new BigDecimal("100.00"))
                    .build();
                bedRepository.save(bed);
            }
        }

        System.out.println("✓ Created " + count + " beds");
    }

    private List<Appointment> createAppointments(List<Patient> patients, List<Doctor> doctors) {
        List<Appointment> appointments = new ArrayList<>();

        // Past appointment - completed
        Appointment apt1 = Appointment.builder()
            .appointmentId("APT-001")
            .patient(patients.get(0))
            .doctor(doctors.get(0))
            .appointmentDate(LocalDate.now().minusDays(7))
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(10, 30))
            .duration(30)
            .type("Consultation")
            .status("COMPLETED")
            .diagnosis("Mild hypertension")
            .treatmentPlan("Prescribed Lisinopril, regular monitoring")
            .notes("Patient responded well to treatment")
            .build();

        // Today's appointment - scheduled
        Appointment apt2 = Appointment.builder()
            .appointmentId("APT-002")
            .patient(patients.get(1))
            .doctor(doctors.get(1))
            .appointmentDate(LocalDate.now())
            .startTime(LocalTime.of(11, 0))
            .endTime(LocalTime.of(11, 30))
            .duration(30)
            .type("Follow-up")
            .status("SCHEDULED")
            .notes("Follow-up for headaches")
            .build();

        // Tomorrow's appointment - scheduled
        Appointment apt3 = Appointment.builder()
            .appointmentId("APT-003")
            .patient(patients.get(2))
            .doctor(doctors.get(2))
            .appointmentDate(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(15, 0))
            .endTime(LocalTime.of(15, 30))
            .duration(30)
            .type("Consultation")
            .status("SCHEDULED")
            .notes("Knee pain evaluation")
            .build();

        // Future appointment - scheduled
        Appointment apt4 = Appointment.builder()
            .appointmentId("APT-004")
            .patient(patients.get(3))
            .doctor(doctors.get(3))
            .appointmentDate(LocalDate.now().plusDays(3))
            .startTime(LocalTime.of(9, 30))
            .endTime(LocalTime.of(10, 0))
            .duration(30)
            .type("Check-up")
            .status("SCHEDULED")
            .notes("Regular pediatric check-up")
            .build();

        appointments.add(appointmentRepository.save(apt1));
        appointments.add(appointmentRepository.save(apt2));
        appointments.add(appointmentRepository.save(apt3));
        appointments.add(appointmentRepository.save(apt4));

        System.out.println("✓ Created " + appointments.size() + " appointments");
        return appointments;
    }

    private void createPrescriptions(List<Patient> patients, List<Doctor> doctors, 
                                    List<Appointment> appointments, List<Medication> medications) {
        // Prescription for completed appointment
        Prescription prescription1 = Prescription.builder()
            .prescriptionId("PRES-001")
            .patient(patients.get(0))
            .doctor(doctors.get(0))
            .dateIssued(LocalDate.now().minusDays(7))
            .status("DISPENSED")
            .instructions("Take one tablet daily with food")
            .build();
        prescription1 = prescriptionRepository.save(prescription1);

        PrescriptionItem item1 = PrescriptionItem.builder()
            .prescriptionItemId("PRITEM-001")
            .prescription(prescription1)
            .medication(medications.get(1)) // Lisinopril
            .quantity(30)
            .build();
        prescriptionItemRepository.save(item1);

        // Prescription for another patient
        Prescription prescription2 = Prescription.builder()
            .prescriptionId("PRES-002")
            .patient(patients.get(2))
            .doctor(doctors.get(2))
            .dateIssued(LocalDate.now().minusDays(3))
            .status("PENDING")
            .instructions("Take as prescribed, 2 tablets daily")
            .build();
        prescription2 = prescriptionRepository.save(prescription2);

        PrescriptionItem item2 = PrescriptionItem.builder()
            .prescriptionItemId("PRITEM-002")
            .prescription(prescription2)
            .medication(medications.get(5)) // Paracetamol
            .quantity(20)
            .build();
        prescriptionItemRepository.save(item2);

        System.out.println("✓ Created 2 prescriptions with items");
    }
}