package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.MedicationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PrescriptionItemRepository prescriptionItemRepository;

    @InjectMocks
    private MedicationService medicationService;

    private Medication testMedication;
    private Pharmacy testPharmacy;
    private Prescription testPrescription;
    private PrescriptionItem testPrescriptionItem;

    @BeforeEach
    void setUp() {
        testPharmacy = new Pharmacy();
        testPharmacy.setPharmacyId("PHAR-001");
        testPharmacy.setLocation("Main Building");

        testMedication = new Medication();
        testMedication.setMedicationId("MED-001");
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
        testPrescriptionItem.setPrescription(testPrescription);
        testPrescriptionItem.setMedication(testMedication);
    }

    // Add Medication Tests
    @Test
    void addMedication_Success() {
        when(medicationRepository.save(any(Medication.class)))
                .thenReturn(testMedication);

        Medication result = medicationService.addMedication(testMedication);

        assertThat(result).isNotNull();
        assertThat(result.getMedicationId()).startsWith("MED-");
        verify(medicationRepository).save(testMedication);
    }

    @Test
    void addMedication_GeneratesMedicationId() {
        when(medicationRepository.save(any(Medication.class)))
                .thenReturn(testMedication);

        Medication result = medicationService.addMedication(testMedication);

        assertThat(result.getMedicationId()).matches("MED-[a-f0-9-]+");
    }

    @Test
    void addMedication_WithAllFields_Success() {
        when(medicationRepository.save(any(Medication.class)))
                .thenReturn(testMedication);

        Medication result = medicationService.addMedication(testMedication);

        assertThat(result.getName()).isEqualTo("Paracetamol");
        assertThat(result.getUnitPrice()).isEqualTo(new BigDecimal("50.00"));
        verify(medicationRepository).save(testMedication);
    }

    // Update Medication Tests
    @Test
    void updateMedication_AllFields_Success() {
        Medication updateData = new Medication();
        updateData.setName("Ibuprofen");
        updateData.setGenericName("Ibuprofen Generic");
        updateData.setManufacturer("MediPharm");
        updateData.setDescription("Anti-inflammatory");
        updateData.setUnitPrice(new BigDecimal("75.00"));
        
        Pharmacy newPharmacy = new Pharmacy();
        newPharmacy.setPharmacyId("PHAR-002");
        updateData.setPharmacy(newPharmacy);

        when(medicationRepository.findById("MED-001"))
                .thenReturn(Optional.of(testMedication));
        when(medicationRepository.save(any(Medication.class)))
                .thenReturn(testMedication);

        Medication result = medicationService.updateMedication("MED-001", updateData);

        assertThat(result).isNotNull();
        verify(medicationRepository).save(testMedication);
    }

    @Test
    void updateMedication_PartialUpdate_Success() {
        Medication updateData = new Medication();
        updateData.setName("Updated Paracetamol");

        when(medicationRepository.findById("MED-001"))
                .thenReturn(Optional.of(testMedication));
        when(medicationRepository.save(any(Medication.class)))
                .thenReturn(testMedication);

        Medication result = medicationService.updateMedication("MED-001", updateData);

        assertThat(result).isNotNull();
        verify(medicationRepository).save(testMedication);
    }

    @Test
    void updateMedication_NullFields_DoesNotUpdate() {
        Medication updateData = new Medication();

        when(medicationRepository.findById("MED-001"))
                .thenReturn(Optional.of(testMedication));
        when(medicationRepository.save(any(Medication.class)))
                .thenReturn(testMedication);

        Medication result = medicationService.updateMedication("MED-001", updateData);

        assertThat(result).isNotNull();
        verify(medicationRepository).save(testMedication);
    }

    @Test
    void updateMedication_NotFound_ThrowsException() {
        when(medicationRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                medicationService.updateMedication("MED-999", new Medication()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found");
    }

    // Delete Medication Tests
    @Test
    void deleteMedication_Success() {
        doNothing().when(medicationRepository).deleteById("MED-001");

        medicationService.deleteMedication("MED-001");

        verify(medicationRepository).deleteById("MED-001");
    }

    @Test
    void deleteMedication_NonExistent_NoException() {
        doNothing().when(medicationRepository).deleteById("MED-999");

        medicationService.deleteMedication("MED-999");

        verify(medicationRepository).deleteById("MED-999");
    }

    // Get Medication Tests
    @Test
    void getMedicationById_Success() {
        when(medicationRepository.findById("MED-001"))
                .thenReturn(Optional.of(testMedication));

        Medication result = medicationService.getMedicationById("MED-001");

        assertThat(result).isNotNull();
        assertThat(result.getMedicationId()).isEqualTo("MED-001");
    }

    @Test
    void getMedicationById_NotFound_ThrowsException() {
        when(medicationRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicationService.getMedicationById("MED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found");
    }

    @Test
    void getMedicationByName_Success() {
        when(medicationRepository.findByName("Paracetamol"))
                .thenReturn(Optional.of(testMedication));

        Medication result = medicationService.getMedicationByName("Paracetamol");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Paracetamol");
    }

    @Test
    void getMedicationByName_NotFound_ThrowsException() {
        when(medicationRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicationService.getMedicationByName("NonExistent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found");
    }

    @Test
    void getAllMedications_Success() {
        Medication med2 = new Medication();
        med2.setMedicationId("MED-002");
        
        List<Medication> medications = Arrays.asList(testMedication, med2);
        when(medicationRepository.findAll())
                .thenReturn(medications);

        List<Medication> result = medicationService.getAllMedications();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testMedication, med2);
    }

    @Test
    void getAllMedications_NoMedications_ReturnsEmptyList() {
        when(medicationRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Medication> result = medicationService.getAllMedications();

        assertThat(result).isEmpty();
    }

    @Test
    void getMedicationsByPharmacy_Success() {
        List<Medication> medications = Arrays.asList(testMedication);
        when(medicationRepository.findByPharmacyId("PHAR-001"))
                .thenReturn(medications);

        List<Medication> result = medicationService.getMedicationsByPharmacy("PHAR-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testMedication);
    }

    @Test
    void getMedicationsByPharmacy_NoMedications_ReturnsEmptyList() {
        when(medicationRepository.findByPharmacyId(anyString()))
                .thenReturn(Collections.emptyList());

        List<Medication> result = medicationService.getMedicationsByPharmacy("PHAR-999");

        assertThat(result).isEmpty();
    }

    @Test
    void searchMedicationsByKeyword_Success() {
        List<Medication> medications = Arrays.asList(testMedication);
        when(medicationRepository.searchByKeyword("Para"))
                .thenReturn(medications);

        List<Medication> result = medicationService.searchMedicationsByKeyword("Para");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).contains("Para");
    }

    @Test
    void searchMedicationsByKeyword_NoMatch_ReturnsEmptyList() {
        when(medicationRepository.searchByKeyword(anyString()))
                .thenReturn(Collections.emptyList());

        List<Medication> result = medicationService.searchMedicationsByKeyword("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void getMedicationsByManufacturer_Success() {
        List<Medication> medications = Arrays.asList(testMedication);
        when(medicationRepository.findByManufacturer("PharmaCorp"))
                .thenReturn(medications);

        List<Medication> result = medicationService.getMedicationsByManufacturer("PharmaCorp");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getManufacturer()).isEqualTo("PharmaCorp");
    }

    @Test
    void getMedicationsByManufacturer_NoMatch_ReturnsEmptyList() {
        when(medicationRepository.findByManufacturer(anyString()))
                .thenReturn(Collections.emptyList());

        List<Medication> result = medicationService.getMedicationsByManufacturer("UnknownCorp");

        assertThat(result).isEmpty();
    }

    @Test
    void getMedicationsByPriceRange_Success() {
        List<Medication> medications = Arrays.asList(testMedication);
        when(medicationRepository.findByPriceRange(
                new BigDecimal("0.00"), new BigDecimal("100.00")))
                .thenReturn(medications);

        List<Medication> result = medicationService.getMedicationsByPriceRange(
                new BigDecimal("0.00"), new BigDecimal("100.00"));

        assertThat(result).hasSize(1);
    }

    @Test
    void getMedicationsByPriceRange_NoMatch_ReturnsEmptyList() {
        when(medicationRepository.findByPriceRange(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Collections.emptyList());

        List<Medication> result = medicationService.getMedicationsByPriceRange(
                new BigDecimal("1000.00"), new BigDecimal("2000.00"));

        assertThat(result).isEmpty();
    }

    // Dispense Medication Tests
    @Test
    void dispenseMedication_Success() {
        testPrescription.setStatus("PENDING");
        
        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionItemRepository.findByPrescriptionId("PRES-001"))
                .thenReturn(Arrays.asList(testPrescriptionItem));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = medicationService.dispenseMedication("PRES-001");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("DISPENSED");
        verify(prescriptionRepository).save(testPrescription);
    }

    @Test
    void dispenseMedication_PrescriptionNotFound_ThrowsException() {
        when(prescriptionRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicationService.dispenseMedication("PRES-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    @Test
    void dispenseMedication_NoItems_Success() {
        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionItemRepository.findByPrescriptionId("PRES-001"))
                .thenReturn(Collections.emptyList());
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = medicationService.dispenseMedication("PRES-001");

        assertThat(result.getStatus()).isEqualTo("DISPENSED");
    }

    @Test
    void dispenseMedication_MedicationNotInItem_ThrowsException() {
        testPrescriptionItem.setMedication(null);

        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionItemRepository.findByPrescriptionId("PRES-001"))
                .thenReturn(Arrays.asList(testPrescriptionItem));

        assertThatThrownBy(() -> medicationService.dispenseMedication("PRES-001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Medication not found in prescription item");
    }

    @Test
    void dispenseMedication_MultipleItems_Success() {
        PrescriptionItem item2 = new PrescriptionItem();
        item2.setMedication(testMedication);

        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionItemRepository.findByPrescriptionId("PRES-001"))
                .thenReturn(Arrays.asList(testPrescriptionItem, item2));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = medicationService.dispenseMedication("PRES-001");

        assertThat(result.getStatus()).isEqualTo("DISPENSED");
    }

    @Test
    void dispenseMedication_AlreadyDispensed_UpdatesAgain() {
        testPrescription.setStatus("DISPENSED");

        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionItemRepository.findByPrescriptionId("PRES-001"))
                .thenReturn(Arrays.asList(testPrescriptionItem));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = medicationService.dispenseMedication("PRES-001");

        assertThat(result.getStatus()).isEqualTo("DISPENSED");
        verify(prescriptionRepository).save(testPrescription);
    }
}