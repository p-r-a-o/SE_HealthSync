package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private PrescriptionItemRepository prescriptionItemRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private MedicationRepository medicationRepository;

    // FR-12: Create/Write Prescription
    public Prescription createPrescription(Prescription prescription) {
        prescription.setPrescriptionId("PRES-" + UUID.randomUUID().toString());
        prescription.setDateIssued(LocalDate.now());
        prescription.setStatus("PENDING");
        return prescriptionRepository.save(prescription);
    }

    // Add items to prescription
    public PrescriptionItem addPrescriptionItem(PrescriptionItem item) {
        item.setPrescriptionItemId("ITEM-" + UUID.randomUUID().toString());
        return prescriptionItemRepository.save(item);
    }

    // Create prescription with items
    public Prescription createPrescriptionWithItems(Prescription prescription, 
                                                   List<PrescriptionItem> items) {
        Prescription savedPrescription = createPrescription(prescription);
        
        for (PrescriptionItem item : items) {
            item.setPrescription(savedPrescription);
            addPrescriptionItem(item);
        }
        
        return savedPrescription;
    }

    // FR-12: View Prescription
    public Prescription getPrescriptionById(String prescriptionId) {
        return prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));
    }

    public List<Prescription> getPrescriptionsByPatient(String patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    public List<Prescription> getPrescriptionsByDoctor(String doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    public List<Prescription> getPrescriptionsByStatus(String status) {
        return prescriptionRepository.findByStatus(status);
    }

    public List<PrescriptionItem> getPrescriptionItems(String prescriptionId) {
        return prescriptionItemRepository.findByPrescriptionId(prescriptionId);
    }

    // FR-13: Update Prescription Status (for dispensing medication)
    public Prescription updatePrescriptionStatus(String prescriptionId, String status) {
        Prescription prescription = getPrescriptionById(prescriptionId);
        prescription.setStatus(status);
        return prescriptionRepository.save(prescription);
    }

    public Prescription updatePrescription(String prescriptionId, Prescription updatedPrescription) {
        Prescription existingPrescription = getPrescriptionById(prescriptionId);

        if (updatedPrescription.getInstructions() != null) {
            existingPrescription.setInstructions(updatedPrescription.getInstructions());
        }
        if (updatedPrescription.getStatus() != null) {
            existingPrescription.setStatus(updatedPrescription.getStatus());
        }

        return prescriptionRepository.save(existingPrescription);
    }

    public void deletePrescription(String prescriptionId) {
        prescriptionRepository.deleteById(prescriptionId);
    }

    public void deletePrescriptionItem(String itemId) {
        prescriptionItemRepository.deleteById(itemId);
    }

    public List<Prescription> getPrescriptionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return prescriptionRepository.findByDateRange(startDate, endDate);
    }

    public List<PrescriptionItem> getPrescriptionItemsByPatient(String patientId) {
        return prescriptionItemRepository.findByPatientId(patientId);
    }
}