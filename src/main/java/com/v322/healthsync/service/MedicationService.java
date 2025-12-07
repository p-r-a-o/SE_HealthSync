package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;
    
    @Autowired
    private PharmacyRepository pharmacyRepository;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private PrescriptionItemRepository prescriptionItemRepository;

    // FR-14: Manage Medication Inventory - Add
    public Medication addMedication(Medication medication) {
        medication.setMedicationId("MED-" + UUID.randomUUID().toString());
        return medicationRepository.save(medication);
    }

    // FR-14: Manage Medication Inventory - Update
    public Medication updateMedication(String medicationId, Medication updatedMedication) {
        Medication existingMedication = medicationRepository.findById(medicationId)
            .orElseThrow(() -> new RuntimeException("Medication not found"));

        if (updatedMedication.getName() != null) {
            existingMedication.setName(updatedMedication.getName());
        }
        if (updatedMedication.getGenericName() != null) {
            existingMedication.setGenericName(updatedMedication.getGenericName());
        }
        if (updatedMedication.getManufacturer() != null) {
            existingMedication.setManufacturer(updatedMedication.getManufacturer());
        }
        if (updatedMedication.getDescription() != null) {
            existingMedication.setDescription(updatedMedication.getDescription());
        }
        if (updatedMedication.getUnitPrice() != null) {
            existingMedication.setUnitPrice(updatedMedication.getUnitPrice());
        }
        if (updatedMedication.getPharmacy() != null) {
            existingMedication.setPharmacy(updatedMedication.getPharmacy());
        }

        return medicationRepository.save(existingMedication);
    }

    // FR-14: Manage Medication Inventory - Remove
    public void deleteMedication(String medicationId) {
        medicationRepository.deleteById(medicationId);
    }

    // FR-15: Check Medication Availability
    public Medication getMedicationById(String medicationId) {
        return medicationRepository.findById(medicationId)
            .orElseThrow(() -> new RuntimeException("Medication not found"));
    }

    public Medication getMedicationByName(String name) {
        return medicationRepository.findByName(name)
            .orElseThrow(() -> new RuntimeException("Medication not found"));
    }

    public List<Medication> getAllMedications() {
        return medicationRepository.findAll();
    }

    public List<Medication> getMedicationsByPharmacy(String pharmacyId) {
        return medicationRepository.findByPharmacyId(pharmacyId);
    }

    public List<Medication> searchMedicationsByKeyword(String keyword) {
        return medicationRepository.searchByKeyword(keyword);
    }

    public List<Medication> getMedicationsByManufacturer(String manufacturer) {
        return medicationRepository.findByManufacturer(manufacturer);
    }

    public List<Medication> getMedicationsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return medicationRepository.findByPriceRange(minPrice, maxPrice);
    }

    // FR-13: Dispense Medication
    public Prescription dispenseMedication(String prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));

        List<PrescriptionItem> items = prescriptionItemRepository
            .findByPrescriptionId(prescriptionId);

        // Check if all medications are available
        for (PrescriptionItem item : items) {
            Medication medication = item.getMedication();
            if (medication == null) {
                throw new RuntimeException("Medication not found in prescription item");
            }
        }

        // Update prescription status to dispensed
        prescription.setStatus("DISPENSED");
        return prescriptionRepository.save(prescription);
    }
}