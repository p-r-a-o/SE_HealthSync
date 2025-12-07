package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.PrescriptionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class PrescriptionServiceTest extends BaseIntegrationTest {

    @Autowired
    PrescriptionRepository prescriptionRepository;

    @Autowired
    PrescriptionItemRepository prescriptionItemRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    MedicationRepository medicationRepository;

    @Autowired
    PrescriptionService prescriptionService;

    private Prescription testPrescription;
    private PrescriptionItem testItem;
    private Patient testPatient;
    private Doctor testDoctor;
    private Medication testMedication;

    @BeforeEach
    void setUp() {
        prescriptionItemRepository.deleteAll();
        prescriptionRepository.deleteAll();
        medicationRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setEmail("patient@test.com");
        testPatient.setPassword("password");
        testPatient = patientRepository.save(testPatient);

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setPassword("password");
        testDoctor = doctorRepository.save(testDoctor);

        testMedication = new Medication();
        testMedication.setMedicationId("MED-001");
        testMedication.setName("Test Med");
        testMedication = medicationRepository.save(testMedication);

        testPrescription = new Prescription();
        testPrescription.setPatient(testPatient);
        testPrescription.setDoctor(testDoctor);
        testPrescription.setDateIssued(LocalDate.now());
        testPrescription.setStatus("PENDING");
        testPrescription.setInstructions("Take after meals");

        testItem = new PrescriptionItem();
        testItem.setMedication(testMedication);
    }

    // Create Prescription Tests
    @Test
    void createPrescription_Success() {
        Prescription result = prescriptionService.createPrescription(testPrescription);

        assertThat(result).isNotNull();
        assertThat(result.getPrescriptionId()).startsWith("PRES-");
        assertThat(result.getDateIssued()).isEqualTo(LocalDate.now());
        assertThat(result.getStatus()).isEqualTo("PENDING");
        
        Prescription saved = prescriptionRepository.findById(result.getPrescriptionId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void createPrescription_SetsDefaultValues() {
        Prescription result = prescriptionService.createPrescription(testPrescription);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getDateIssued()).isEqualTo(LocalDate.now());
        assertThat(result.getPrescriptionId()).startsWith("PRES-");
    }

    // Add Prescription Item Tests
    @Test
    void addPrescriptionItem_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        testItem.setPrescription(saved);

        PrescriptionItem result = prescriptionService.addPrescriptionItem(testItem);

        assertThat(result).isNotNull();
        assertThat(result.getPrescriptionItemId()).startsWith("ITEM-");
        
        PrescriptionItem savedItem = prescriptionItemRepository.findById(result.getPrescriptionItemId()).orElse(null);
        assertThat(savedItem).isNotNull();
    }

    @Test
    void addPrescriptionItem_GeneratesItemId() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        testItem.setPrescription(saved);

        PrescriptionItem result = prescriptionService.addPrescriptionItem(testItem);

        assertThat(result.getPrescriptionItemId()).matches("ITEM-[a-f0-9-]+");
    }

    // Create Prescription With Items Tests
    @Test
    void createPrescriptionWithItems_EmptyItems_Success() {
        Prescription result = prescriptionService.createPrescriptionWithItems(
                testPrescription, Collections.emptyList());

        assertThat(result).isNotNull();
        
        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescriptionId(result.getPrescriptionId());
        assertThat(items).isEmpty();
    }

    @Test
    void createPrescriptionWithItems_AssignsItemIds() {
        PrescriptionItem item = new PrescriptionItem();
        item.setMedication(testMedication);

        Prescription result = prescriptionService.createPrescriptionWithItems(
                testPrescription, Arrays.asList(item));

        List<PrescriptionItem> savedItems = prescriptionItemRepository.findByPrescriptionId(result.getPrescriptionId());
        assertThat(savedItems).hasSize(1);
        assertThat(savedItems.get(0).getPrescriptionItemId()).startsWith("ITEM-");
    }

    // Get Prescription Tests
    @Test
    void getPrescriptionById_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);

        Prescription result = prescriptionService.getPrescriptionById(saved.getPrescriptionId());

        assertThat(result).isNotNull();
        assertThat(result.getPrescriptionId()).isEqualTo(saved.getPrescriptionId());
    }

    @Test
    void getPrescriptionById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> prescriptionService.getPrescriptionById("PRES-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    @Test
    void getPrescriptionsByPatient_Success() {
        prescriptionService.createPrescription(testPrescription);

        List<Prescription> result = prescriptionService.getPrescriptionsByPatient(testPatient.getPersonId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPrescriptionsByPatient_NoPrescriptions_ReturnsEmptyList() {
        List<Prescription> result = prescriptionService.getPrescriptionsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void getPrescriptionsByDoctor_Success() {
        prescriptionService.createPrescription(testPrescription);

        List<Prescription> result = prescriptionService.getPrescriptionsByDoctor(testDoctor.getPersonId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPrescriptionsByStatus_Success() {
        prescriptionService.createPrescription(testPrescription);

        List<Prescription> result = prescriptionService.getPrescriptionsByStatus("PENDING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void getPrescriptionItems_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        testItem.setPrescription(saved);
        prescriptionService.addPrescriptionItem(testItem);

        List<PrescriptionItem> result = prescriptionService.getPrescriptionItems(saved.getPrescriptionId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPrescriptionItems_NoItems_ReturnsEmptyList() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);

        List<PrescriptionItem> result = prescriptionService.getPrescriptionItems(saved.getPrescriptionId());

        assertThat(result).isEmpty();
    }

    // Update Prescription Status Tests
    @Test
    void updatePrescriptionStatus_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);

        Prescription result = prescriptionService.updatePrescriptionStatus(saved.getPrescriptionId(), "DISPENSED");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("DISPENSED");
    }

    @Test
    void updatePrescriptionStatus_PrescriptionNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                prescriptionService.updatePrescriptionStatus("PRES-999", "DISPENSED"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    @Test
    void updatePrescriptionStatus_VariousStatuses_Success() {
        String[] statuses = {"PENDING", "DISPENSED", "CANCELLED", "EXPIRED"};

        for (String status : statuses) {
            Prescription saved = prescriptionService.createPrescription(testPrescription);
            
            Prescription result = prescriptionService.updatePrescriptionStatus(saved.getPrescriptionId(), status);

            assertThat(result.getStatus()).isEqualTo(status);
            
            // Clean up for next iteration
            prescriptionRepository.delete(saved);
        }
    }

    // Update Prescription Tests
    @Test
    void updatePrescription_UpdateInstructions_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        
        Prescription updateData = new Prescription();
        updateData.setInstructions("Take before meals");

        Prescription result = prescriptionService.updatePrescription(saved.getPrescriptionId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getInstructions()).isEqualTo("Take before meals");
    }

    @Test
    void updatePrescription_UpdateStatus_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        
        Prescription updateData = new Prescription();
        updateData.setStatus("COMPLETED");

        Prescription result = prescriptionService.updatePrescription(saved.getPrescriptionId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void updatePrescription_NullFields_DoesNotUpdate() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        String originalInstructions = saved.getInstructions();
        
        Prescription updateData = new Prescription();

        Prescription result = prescriptionService.updatePrescription(saved.getPrescriptionId(), updateData);

        assertThat(result.getInstructions()).isEqualTo(originalInstructions);
    }

    @Test
    void updatePrescription_NotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                prescriptionService.updatePrescription("PRES-999", new Prescription()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    // Delete Prescription Tests
    @Test
    void deletePrescription_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);

        prescriptionService.deletePrescription(saved.getPrescriptionId());

        assertThat(prescriptionRepository.findById(saved.getPrescriptionId())).isEmpty();
    }

    @Test
    void deletePrescriptionItem_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        testItem.setPrescription(saved);
        PrescriptionItem savedItem = prescriptionService.addPrescriptionItem(testItem);

        prescriptionService.deletePrescriptionItem(savedItem.getPrescriptionItemId());

        assertThat(prescriptionItemRepository.findById(savedItem.getPrescriptionItemId())).isEmpty();
    }

    // Get Prescriptions By Date Range Tests
    @Test
    void getPrescriptionsByDateRange_Success() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        prescriptionService.createPrescription(testPrescription);

        List<Prescription> result = prescriptionService.getPrescriptionsByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
    }

    @Test
    void getPrescriptionsByDateRange_NoPrescriptions_ReturnsEmptyList() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 12, 31);

        List<Prescription> result = prescriptionService.getPrescriptionsByDateRange(startDate, endDate);

        assertThat(result).isEmpty();
    }

    // Get Prescription Items By Patient Tests
    @Test
    void getPrescriptionItemsByPatient_Success() {
        Prescription saved = prescriptionService.createPrescription(testPrescription);
        testItem.setPrescription(saved);
        prescriptionService.addPrescriptionItem(testItem);

        List<PrescriptionItem> result = prescriptionService.getPrescriptionItemsByPatient(testPatient.getPersonId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getPrescriptionItemsByPatient_NoItems_ReturnsEmptyList() {
        List<PrescriptionItem> result = prescriptionService.getPrescriptionItemsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }
}