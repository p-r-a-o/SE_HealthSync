package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private BillRepository billRepository;


    // FR-1: Patient Registration
    public Patient registerPatient(Patient patient) {
        patient.setRegistrationDate(LocalDate.now());
        return patientRepository.save(patient);
    }

    // FR-1: Update Patient Information
    public Patient updatePatient(String patientId, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        existingPatient.setFirstName(updatedPatient.getFirstName());
        existingPatient.setLastName(updatedPatient.getLastName());
        existingPatient.setDateOfBirth(updatedPatient.getDateOfBirth());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setBloodGroup(updatedPatient.getBloodGroup());
        existingPatient.setContactNumber(updatedPatient.getContactNumber());
        existingPatient.setEmail(updatedPatient.getEmail());
        existingPatient.setCity(updatedPatient.getCity());
        existingPatient.setNotes(updatedPatient.getNotes());
        
        return patientRepository.save(existingPatient);
    }

    public Patient getPatientById(String patientId) {
        return patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> searchPatientsByName(String name) {
        return patientRepository.searchByName(name);
    }

    public List<Patient> getPatientsByCity(String city) {
        return patientRepository.findByCity(city);
    }

    public List<Patient> getPatientsByBloodGroup(String bloodGroup) {
        return patientRepository.findByBloodGroup(bloodGroup);
    }

    // FR-2: View Medical History
    public MedicalHistory getMedicalHistory(String patientId) {
        Patient patient = getPatientById(patientId);
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        List<Bill> bills = billRepository.findByPatientId(patientId);
        
        return new MedicalHistory(patient, appointments, prescriptions, bills);
    }
    // FR-2: View Medical HistoryDTO
    public MedicalHistoryDTO getMedicalHistoryDTO(String patientId) {
        Patient patient = getPatientById(patientId);
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        List<Bill> bills = billRepository.findByPatientId(patientId);
        
        return new MedicalHistoryDTO(patient, appointments, prescriptions, bills);
    }

    public void deletePatient(String patientId) {
        patientRepository.deleteById(patientId);
    }

    // Inner class for medical history response
    public static class MedicalHistory {
        private Patient patient;
        private List<Appointment> appointments;
        private List<Prescription> prescriptions;
        private List<Bill> bills;

        public MedicalHistory(Patient patient, List<Appointment> appointments, 
                            List<Prescription> prescriptions, List<Bill> bills) {
            this.patient = patient;
            this.appointments = appointments;
            this.prescriptions = prescriptions;
            this.bills = bills;
        }

        // Getters
        public Patient getPatient() { return patient; }
        public List<Appointment> getAppointments() { return appointments; }
        public List<Prescription> getPrescriptions() { return prescriptions; }
        public List<Bill> getBills() { return bills; }
    }

    // Inner class for medical history response
    public static class MedicalHistoryDTO {
        private PatientDTO patient;
        private List<AppointmentDTO> appointments;
        private List<PrescriptionDTO> prescriptions;
        private List<BillDTO> bills;


        public MedicalHistoryDTO(Patient patient, List<Appointment> appointments, 
                            List<Prescription> prescriptions, List<Bill> bills) {
            this.patient = DTOMapper.toPatientDTO(patient);
            this.appointments = appointments.stream().map(DTOMapper::toAppointmentDTO).toList();
            this.prescriptions = prescriptions.stream().map(DTOMapper::toPrescriptionDTO).toList();
            this.bills = bills.stream().map(DTOMapper::toBillDTO).toList();
        }

        // Getters
        public PatientDTO getPatient() { return patient; }
        public List<AppointmentDTO> getAppointments() { return appointments; }
        public List<PrescriptionDTO> getPrescriptions() { return prescriptions; }
        public List<BillDTO> getBills() { return bills; }
    }
}