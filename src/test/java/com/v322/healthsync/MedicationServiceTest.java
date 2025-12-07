package com.v322.healthsync;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.v322.healthsync.entity.Medication;
import com.v322.healthsync.entity.Pharmacy;
import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.entity.PrescriptionItem;
import com.v322.healthsync.repository.MedicationRepository;
import com.v322.healthsync.repository.PharmacyRepository;
import com.v322.healthsync.repository.PrescriptionItemRepository;
import com.v322.healthsync.repository.PrescriptionRepository;
import com.v322.healthsync.service.MedicationService;

class MedicationServiceTest extends BaseIntegrationTest {

    @Autowired
    MedicationRepository medicationRepository;

    @Autowired
    PharmacyRepository pharmacyRepository;

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    PrescriptionItemRepository prescriptionItemRepository;

    @Autowired
    MedicationService medicationService;

    private Medication testMedication;
    private Pharmacy testPharmacy;
    private Prescription testPrescription;
    private PrescriptionItem testPrescriptionItem;

    @BeforeEach
    void setUp() {
        prescriptionItemRepository.deleteAll();
        prescriptionRepository.deleteAll();
        medicationRepository.deleteAll();
        pharmacyRepository.deleteAll();

        testPharmacy = new Pharmacy();
        testPharmacy.setPharmacyId("PHAR-001");
        testPharmacy.setLocation("Main Building");
        testPharmacy = pharmacyRepository.save(testPharmacy);

        testMedication = new Medication();
        testMedication.setName("Paracetamol");
        testMedication.setGenericName("Acetaminophen");
        testMedication.setManufacturer("PharmaCorp");
        testMedication.setDescription("Pain reliever");
        testMedication.setUnitPrice(new BigDecimal("50.00"));
        testMedication.setPharmacy(testPharmacy);

        testPrescription = new Prescription();
        testPrescription.setPrescriptionId("PRES-001");
        testPrescription.setStatus("PENDING");

        testPrescriptionItem = new PrescriptionItem();
        testPrescriptionItem.setPrescriptionItemId("ITEM-001");
    }

    // Add Medication Tests
    @Test
    void addMedication_Success() {
        Medication result = medicationService.addMedication(testMedication);

        assertThat(result).isNotNull();
        assertThat(result.getMedicationId()).startsWith("MED-");
        
        Medication saved = medicationRepository.findById(result.getMedicationId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void addMedication_GeneratesMedicationId() {
        Medication result = medicationService.addMedication(testMedication);

        assertThat(result.getMedicationId()).matches("MED-[a-f0-9-]+");
    }

    @Test
    void addMedication_WithAllFields_Success() {
        Medication result = medicationService.addMedication(testMedication);

        assertThat(result.getName()).isEqualTo("Paracetamol");
        assertThat(result.getUnitPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    // Update Medication Tests
    @Test
    void updateMedication_AllFields_Success() {
        Medication saved = medicationService.addMedication(testMedication);
        
        Pharmacy newPharmacy = new Pharmacy();
        newPharmacy.setPharmacyId("PHAR-002");
        newPharmacy.setLocation("North Building");
        newPharmacy = pharmacyRepository.save(newPharmacy);
        
        Medication updateData = new Medication();
        updateData.setName("Ibuprofen");
        updateData.setGenericName("Ibuprofen Generic");
        updateData.setManufacturer("MediPharm");
        updateData.setDescription("Anti-inflammatory");
        updateData.setUnitPrice(new BigDecimal("75.00"));
        updateData.setPharmacy(newPharmacy);

        Medication result = medicationService.updateMedication(saved.getMedicationId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Ibuprofen");
        assertThat(result.getUnitPrice()).isEqualByComparingTo(new BigDecimal("75.00"));
    }

    @Test
    void updateMedication_PartialUpdate_Success() {
        Medication saved = medicationService.addMedication(testMedication);
        
        Medication updateData = new Medication();
        updateData.setName("Updated Paracetamol");

        Medication result = medicationService.updateMedication(saved.getMedicationId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Paracetamol");
    }

    @Test
    void updateMedication_NullFields_DoesNotUpdate() {
        Medication saved = medicationService.addMedication(testMedication);
        String originalName = saved.getName();
        
        Medication updateData = new Medication();

        Medication result = medicationService.updateMedication(saved.getMedicationId(), updateData);

        assertThat(result.getName()).isEqualTo(originalName);
    }

    @Test
    void updateMedication_NotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                medicationService.updateMedication("MED-999", new Medication()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found");
    }

    // Delete Medication Tests
    @Test
    void deleteMedication_Success() {
        Medication saved = medicationService.addMedication(testMedication);

        medicationService.deleteMedication(saved.getMedicationId());

        assertThat(medicationRepository.findById(saved.getMedicationId())).isEmpty();
    }

    @Test
    void deleteMedication_NonExistent_NoException() {
        medicationService.deleteMedication("MED-999");

        // No exception thrown
    }

    // Get Medication Tests
    @Test
    void getMedicationById_Success() {
        Medication saved = medicationService.addMedication(testMedication);

        Medication result = medicationService.getMedicationById(saved.getMedicationId());

        assertThat(result).isNotNull();
        assertThat(result.getMedicationId()).isEqualTo(saved.getMedicationId());
    }

    @Test
    void getMedicationById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> medicationService.getMedicationById("MED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found");
    }

    @Test
    void getMedicationByName_Success() {
        medicationService.addMedication(testMedication);

        Medication result = medicationService.getMedicationByName("Paracetamol");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Paracetamol");
    }

    @Test
    void getMedicationByName_NotFound_ThrowsException() {
        assertThatThrownBy(() -> medicationService.getMedicationByName("NonExistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found");
    }

    @Test
    void getAllMedications_Success() {
        medicationService.addMedication(testMedication);
        
        Medication med2 = new Medication();
        med2.setName("Aspirin");
        med2.setGenericName("Aspirin Generic");
        med2.setManufacturer("PharmaCorp");
        med2.setUnitPrice(new BigDecimal("30.00"));
        med2.setPharmacy(testPharmacy);
        medicationService.addMedication(med2);

        List<Medication> result = medicationService.getAllMedications();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllMedications_NoMedications_ReturnsEmptyList() {
        List<Medication> result = medicationService.getAllMedications();

        assertThat(result).isEmpty();
    }

    @Test
    void getMedicationsByPharmacy_Success() {
        medicationService.addMedication(testMedication);

        List<Medication> result = medicationService.getMedicationsByPharmacy(testPharmacy.getPharmacyId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getMedicationsByPharmacy_NoMedications_ReturnsEmptyList() {
        List<Medication> result = medicationService.getMedicationsByPharmacy("PHAR-999");

        assertThat(result).isEmpty();
    }

    @Test
    void searchMedicationsByKeyword_Success() {
        medicationService.addMedication(testMedication);

        List<Medication> result = medicationService.searchMedicationsByKeyword("Para");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchMedicationsByKeyword_NoMatch_ReturnsEmptyList() {
        medicationService.addMedication(testMedication);

        List<Medication> result = medicationService.searchMedicationsByKeyword("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void getMedicationsByManufacturer_Success() {
        medicationService.addMedication(testMedication);

        List<Medication> result = medicationService.getMedicationsByManufacturer("PharmaCorp");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getManufacturer()).isEqualTo("PharmaCorp");
    }

    @Test
    void getMedicationsByManufacturer_NoMatch_ReturnsEmptyList() {
        medicationService.addMedication(testMedication);

        List<Medication> result = medicationService.getMedicationsByManufacturer("UnknownCorp");

        assertThat(result).isEmpty();
    }

    @Test
    void getMedicationsByPriceRange_Success() {
        medicationService.addMedication(testMedication);

        List<Medication> result = medicationService.getMedicationsByPriceRange(
                new BigDecimal("0.00"), new BigDecimal("100.00"));

        assertThat(result).hasSize(1);
    }

    @Test
    void getMedicationsByPriceRange_NoMatch_ReturnsEmptyList() {
        medicationService.addMedication(testMedication);

        List<Medication> result = medicationService.getMedicationsByPriceRange(
                new BigDecimal("1000.00"), new BigDecimal("2000.00"));

        assertThat(result).isEmpty();
    }

    // Dispense Medication Tests
    @Test
    void dispenseMedication_Success() {
        Medication savedMed = medicationService.addMedication(testMedication);
        
        testPrescription.setStatus("PENDING");
        testPrescription = prescriptionRepository.save(testPrescription);
        
        testPrescriptionItem.setPrescription(testPrescription);
        testPrescriptionItem.setMedication(savedMed);
        prescriptionItemRepository.save(testPrescriptionItem);

        Prescription result = medicationService.dispenseMedication(testPrescription.getPrescriptionId());

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("DISPENSED");
    }

    @Test
    void dispenseMedication_PrescriptionNotFound_ThrowsException() {
        assertThatThrownBy(() -> medicationService.dispenseMedication("PRES-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    @Test
    void dispenseMedication_NoItems_Success() {
        testPrescription = prescriptionRepository.save(testPrescription);

        Prescription result = medicationService.dispenseMedication(testPrescription.getPrescriptionId());

        assertThat(result.getStatus()).isEqualTo("DISPENSED");
    }

    @Test
    void dispenseMedication_MedicationNotInItem_ThrowsException() {
        testPrescription = prescriptionRepository.save(testPrescription);
        
        testPrescriptionItem.setPrescription(testPrescription);
        testPrescriptionItem.setMedication(null);
        prescriptionItemRepository.save(testPrescriptionItem);

        assertThatThrownBy(() -> medicationService.dispenseMedication(testPrescription.getPrescriptionId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found in prescription item");
    }

    @Test
    void dispenseMedication_MultipleItems_Success() {
        Medication savedMed = medicationService.addMedication(testMedication);
        
        testPrescription = prescriptionRepository.save(testPrescription);
        
        testPrescriptionItem.setPrescription(testPrescription);
        testPrescriptionItem.setMedication(savedMed);
        prescriptionItemRepository.save(testPrescriptionItem);
        
        PrescriptionItem item2 = new PrescriptionItem();
        item2.setPrescriptionItemId("ITEM-002");
        item2.setPrescription(testPrescription);
        item2.setMedication(savedMed);
        prescriptionItemRepository.save(item2);

        Prescription result = medicationService.dispenseMedication(testPrescription.getPrescriptionId());

        assertThat(result.getStatus()).isEqualTo("DISPENSED");
    }

    @Test
    void dispenseMedication_AlreadyDispensed_UpdatesAgain() {
        Medication savedMed = medicationService.addMedication(testMedication);
        
        testPrescription.setStatus("DISPENSED");
        testPrescription = prescriptionRepository.save(testPrescription);
        
        testPrescriptionItem.setPrescription(testPrescription);
        testPrescriptionItem.setMedication(savedMed);
        prescriptionItemRepository.save(testPrescriptionItem);

        Prescription result = medicationService.dispenseMedication(testPrescription.getPrescriptionId());

        assertThat(result.getStatus()).isEqualTo("DISPENSED");
    }
}