package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.PrescriptionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PrescriptionItemRepository prescriptionItemRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private Prescription testPrescription;
    private PrescriptionItem testItem;
    private Patient testPatient;
    private Doctor testDoctor;
    private Medication testMedication;

    @BeforeEach
    void setUp() {
        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");

        testDoctor = new Doctor();
        testDoctor.setPersonId("DOC-001");

        testMedication = new Medication();
        testMedication.setMedicationId("MED-001");

        testPrescription = new Prescription();
        testPrescription.setPrescriptionId("PRES-001");
        testPrescription.setPatient(testPatient);
        testPrescription.setDoctor(testDoctor);
        testPrescription.setDateIssued(LocalDate.now());
        testPrescription.setStatus("PENDING");
        testPrescription.setInstructions("Take after meals");

        testItem = new PrescriptionItem();
        testItem.setPrescriptionItemId("ITEM-001");
        testItem.setPrescription(testPrescription);
        testItem.setMedication(testMedication);
        // testItem.setDosage("500mg");
        // testItem.setFrequency("Twice daily");
        // testItem.setDuration("7 days");
    }

    // Create Prescription Tests
    @Test
    void createPrescription_Success() {
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = prescriptionService.createPrescription(testPrescription);

        assertThat(result).isNotNull();
        assertThat(result.getPrescriptionId()).startsWith("PRES-");
        assertThat(result.getDateIssued()).isEqualTo(LocalDate.now());
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(prescriptionRepository).save(testPrescription);
    }

    @Test
    void createPrescription_SetsDefaultValues() {
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenAnswer(invocation -> {
                    Prescription pres = invocation.getArgument(0);
                    assertThat(pres.getStatus()).isEqualTo("PENDING");
                    assertThat(pres.getDateIssued()).isEqualTo(LocalDate.now());
                    assertThat(pres.getPrescriptionId()).startsWith("PRES-");
                    return pres;
                });

        prescriptionService.createPrescription(testPrescription);

        verify(prescriptionRepository).save(testPrescription);
    }

    // Add Prescription Item Tests
    @Test
    void addPrescriptionItem_Success() {
        when(prescriptionItemRepository.save(any(PrescriptionItem.class)))
                .thenReturn(testItem);

        PrescriptionItem result = prescriptionService.addPrescriptionItem(testItem);

        assertThat(result).isNotNull();
        assertThat(result.getPrescriptionItemId()).startsWith("ITEM-");
        verify(prescriptionItemRepository).save(testItem);
    }

    @Test
    void addPrescriptionItem_GeneratesItemId() {
        when(prescriptionItemRepository.save(any(PrescriptionItem.class)))
                .thenReturn(testItem);

        PrescriptionItem result = prescriptionService.addPrescriptionItem(testItem);

        assertThat(result.getPrescriptionItemId()).matches("ITEM-[a-f0-9-]+");
    }

    // Create Prescription With Items Tests
    // @Test
    // void createPrescriptionWithItems_Success() {
    //     PrescriptionItem item1 = new PrescriptionItem();
    //     item1.setDosage("250mg");
        
    //     PrescriptionItem item2 = new PrescriptionItem();
    //     item2.setDosage("100mg");

    //     List<PrescriptionItem> items = Arrays.asList(item1, item2);

    //     when(prescriptionRepository.save(any(Prescription.class)))
    //             .thenReturn(testPrescription);
    //     when(prescriptionItemRepository.save(any(PrescriptionItem.class)))
    //             .thenAnswer(invocation -> invocation.getArgument(0));

    //     Prescription result = prescriptionService.createPrescriptionWithItems(testPrescription, items);

    //     assertThat(result).isNotNull();
    //     verify(prescriptionRepository).save(testPrescription);
    //     verify(prescriptionItemRepository, times(2)).save(any(PrescriptionItem.class));
    // }

    @Test
    void createPrescriptionWithItems_EmptyItems_Success() {
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = prescriptionService.createPrescriptionWithItems(
                testPrescription, Collections.emptyList());

        assertThat(result).isNotNull();
        verify(prescriptionItemRepository, never()).save(any());
    }

    @Test
    void createPrescriptionWithItems_AssignsItemIds() {
        PrescriptionItem item = new PrescriptionItem();
        
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);
        when(prescriptionItemRepository.save(any(PrescriptionItem.class)))
                .thenAnswer(invocation -> {
                    PrescriptionItem savedItem = invocation.getArgument(0);
                    assertThat(savedItem.getPrescriptionItemId()).startsWith("ITEM-");
                    assertThat(savedItem.getPrescription()).isEqualTo(testPrescription);
                    return savedItem;
                });

        prescriptionService.createPrescriptionWithItems(testPrescription, Arrays.asList(item));

        verify(prescriptionItemRepository).save(any(PrescriptionItem.class));
    }

    // Get Prescription Tests
    @Test
    void getPrescriptionById_Success() {
        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));

        Prescription result = prescriptionService.getPrescriptionById("PRES-001");

        assertThat(result).isNotNull();
        assertThat(result.getPrescriptionId()).isEqualTo("PRES-001");
    }

    @Test
    void getPrescriptionById_NotFound_ThrowsException() {
        when(prescriptionRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> prescriptionService.getPrescriptionById("PRES-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    @Test
    void getPrescriptionsByPatient_Success() {
        List<Prescription> prescriptions = Arrays.asList(testPrescription);
        when(prescriptionRepository.findByPatientId("PAT-001"))
                .thenReturn(prescriptions);

        List<Prescription> result = prescriptionService.getPrescriptionsByPatient("PAT-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPrescription);
    }

    @Test
    void getPrescriptionsByPatient_NoPrescriptions_ReturnsEmptyList() {
        when(prescriptionRepository.findByPatientId(anyString()))
                .thenReturn(Collections.emptyList());

        List<Prescription> result = prescriptionService.getPrescriptionsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void getPrescriptionsByDoctor_Success() {
        List<Prescription> prescriptions = Arrays.asList(testPrescription);
        when(prescriptionRepository.findByDoctorId("DOC-001"))
                .thenReturn(prescriptions);

        List<Prescription> result = prescriptionService.getPrescriptionsByDoctor("DOC-001");

        assertThat(result).hasSize(1);
    }

    @Test
    void getPrescriptionsByStatus_Success() {
        List<Prescription> prescriptions = Arrays.asList(testPrescription);
        when(prescriptionRepository.findByStatus("PENDING"))
                .thenReturn(prescriptions);

        List<Prescription> result = prescriptionService.getPrescriptionsByStatus("PENDING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void getPrescriptionItems_Success() {
        List<PrescriptionItem> items = Arrays.asList(testItem);
        when(prescriptionItemRepository.findByPrescriptionId("PRES-001"))
                .thenReturn(items);

        List<PrescriptionItem> result = prescriptionService.getPrescriptionItems("PRES-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testItem);
    }

    @Test
    void getPrescriptionItems_NoItems_ReturnsEmptyList() {
        when(prescriptionItemRepository.findByPrescriptionId(anyString()))
                .thenReturn(Collections.emptyList());

        List<PrescriptionItem> result = prescriptionService.getPrescriptionItems("PRES-999");

        assertThat(result).isEmpty();
    }

    // Update Prescription Status Tests
    @Test
    void updatePrescriptionStatus_Success() {
        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = prescriptionService.updatePrescriptionStatus("PRES-001", "DISPENSED");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("DISPENSED");
        verify(prescriptionRepository).save(testPrescription);
    }

    @Test
    void updatePrescriptionStatus_PrescriptionNotFound_ThrowsException() {
        when(prescriptionRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                prescriptionService.updatePrescriptionStatus("PRES-999", "DISPENSED"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    @Test
    void updatePrescriptionStatus_VariousStatuses_Success() {
        String[] statuses = {"PENDING", "DISPENSED", "CANCELLED", "EXPIRED"};

        for (String status : statuses) {
            when(prescriptionRepository.findById("PRES-001"))
                    .thenReturn(Optional.of(testPrescription));
            when(prescriptionRepository.save(any(Prescription.class)))
                    .thenReturn(testPrescription);

            Prescription result = prescriptionService.updatePrescriptionStatus("PRES-001", status);

            assertThat(result.getStatus()).isEqualTo(status);
        }
    }

    // Update Prescription Tests
    @Test
    void updatePrescription_UpdateInstructions_Success() {
        Prescription updateData = new Prescription();
        updateData.setInstructions("Take before meals");

        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = prescriptionService.updatePrescription("PRES-001", updateData);

        assertThat(result).isNotNull();
        verify(prescriptionRepository).save(testPrescription);
    }

    @Test
    void updatePrescription_UpdateStatus_Success() {
        Prescription updateData = new Prescription();
        updateData.setStatus("COMPLETED");

        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = prescriptionService.updatePrescription("PRES-001", updateData);

        assertThat(result).isNotNull();
        verify(prescriptionRepository).save(testPrescription);
    }

    @Test
    void updatePrescription_NullFields_DoesNotUpdate() {
        Prescription updateData = new Prescription();

        when(prescriptionRepository.findById("PRES-001"))
                .thenReturn(Optional.of(testPrescription));
        when(prescriptionRepository.save(any(Prescription.class)))
                .thenReturn(testPrescription);

        Prescription result = prescriptionService.updatePrescription("PRES-001", updateData);

        assertThat(result).isNotNull();
        verify(prescriptionRepository).save(testPrescription);
    }

    @Test
    void updatePrescription_NotFound_ThrowsException() {
        when(prescriptionRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                prescriptionService.updatePrescription("PRES-999", new Prescription()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Prescription not found");
    }

    // Delete Prescription Tests
    @Test
    void deletePrescription_Success() {
        doNothing().when(prescriptionRepository).deleteById("PRES-001");

        prescriptionService.deletePrescription("PRES-001");

        verify(prescriptionRepository).deleteById("PRES-001");
    }

    @Test
    void deletePrescriptionItem_Success() {
        doNothing().when(prescriptionItemRepository).deleteById("ITEM-001");

        prescriptionService.deletePrescriptionItem("ITEM-001");

        verify(prescriptionItemRepository).deleteById("ITEM-001");
    }

    // Get Prescriptions By Date Range Tests
    @Test
    void getPrescriptionsByDateRange_Success() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<Prescription> prescriptions = Arrays.asList(testPrescription);

        when(prescriptionRepository.findByDateRange(startDate, endDate))
                .thenReturn(prescriptions);

        List<Prescription> result = prescriptionService.getPrescriptionsByDateRange(startDate, endDate);

        assertThat(result).hasSize(1);
        verify(prescriptionRepository).findByDateRange(startDate, endDate);
    }

    @Test
    void getPrescriptionsByDateRange_NoPrescriptions_ReturnsEmptyList() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        when(prescriptionRepository.findByDateRange(startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<Prescription> result = prescriptionService.getPrescriptionsByDateRange(startDate, endDate);

        assertThat(result).isEmpty();
    }

    // Get Prescription Items By Patient Tests
    @Test
    void getPrescriptionItemsByPatient_Success() {
        List<PrescriptionItem> items = Arrays.asList(testItem);
        when(prescriptionItemRepository.findByPatientId("PAT-001"))
                .thenReturn(items);

        List<PrescriptionItem> result = prescriptionService.getPrescriptionItemsByPatient("PAT-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testItem);
    }

    @Test
    void getPrescriptionItemsByPatient_NoItems_ReturnsEmptyList() {
        when(prescriptionItemRepository.findByPatientId(anyString()))
                .thenReturn(Collections.emptyList());

        List<PrescriptionItem> result = prescriptionService.getPrescriptionItemsByPatient("PAT-999");

        assertThat(result).isEmpty();
    }
}