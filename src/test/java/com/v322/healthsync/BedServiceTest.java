package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.BedService;

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
class BedServiceTest {

    @Mock
    private BedRepository bedRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private BedService bedService;

    private Bed testBed;
    private Patient testPatient;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setDepartmentId("DEPT-001");
        testDepartment.setName("Cardiology");

        testBed = new Bed();
        testBed.setBedId("BED-001");
        testBed.setDepartment(testDepartment);
        testBed.setDailyRate(new BigDecimal("500.00"));
        testBed.setIsOccupied(false);

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
    }

    // Create Bed Tests
    @Test
    void createBed_Success() {
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.createBed(testBed);

        assertThat(result).isNotNull();
        assertThat(result.getBedId()).startsWith("BED-");
        assertThat(result.getIsOccupied()).isFalse();
        verify(bedRepository).save(testBed);
    }

    @Test
    void createBed_SetsOccupiedToFalse() {
        testBed.setIsOccupied(true);
        
        when(bedRepository.save(any(Bed.class)))
                .thenAnswer(invocation -> {
                    Bed bed = invocation.getArgument(0);
                    assertThat(bed.getIsOccupied()).isFalse();
                    return bed;
                });

        bedService.createBed(testBed);

        verify(bedRepository).save(testBed);
    }

    @Test
    void createBed_GeneratesUniqueBedId() {
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.createBed(testBed);

        assertThat(result.getBedId()).matches("BED-[a-f0-9-]+");
    }

    // Assign Bed to Patient Tests
    @Test
    void assignBedToPatient_Success() {
        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));
        when(bedRepository.findByPatientId("PAT-001"))
                .thenReturn(Optional.empty());
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.assignBedToPatient("BED-001", "PAT-001");

        assertThat(result).isNotNull();
        assertThat(result.getIsOccupied()).isTrue();
        assertThat(result.getPatient()).isEqualTo(testPatient);
        verify(bedRepository).save(testBed);
    }

    @Test
    void assignBedToPatient_BedNotFound_ThrowsException() {
        when(bedRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.assignBedToPatient("BED-999", "PAT-001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");

        verify(bedRepository, never()).save(any());
    }

    @Test
    void assignBedToPatient_BedAlreadyOccupied_ThrowsException() {
        testBed.setIsOccupied(true);
        
        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));

        assertThatThrownBy(() -> bedService.assignBedToPatient("BED-001", "PAT-001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed is already occupied");

        verify(bedRepository, never()).save(any());
    }

    @Test
    void assignBedToPatient_PatientNotFound_ThrowsException() {
        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        when(patientRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.assignBedToPatient("BED-001", "PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");

        verify(bedRepository, never()).save(any());
    }

    @Test
    void assignBedToPatient_PatientAlreadyHasBed_ThrowsException() {
        Bed existingBed = new Bed();
        existingBed.setBedId("BED-002");

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        when(patientRepository.findById("PAT-001"))
                .thenReturn(Optional.of(testPatient));
        when(bedRepository.findByPatientId("PAT-001"))
                .thenReturn(Optional.of(existingBed));

        assertThatThrownBy(() -> bedService.assignBedToPatient("BED-001", "PAT-001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient already has a bed assigned");

        verify(bedRepository, never()).save(any());
    }

    // Release Bed Tests
    @Test
    void releaseBed_Success() {
        testBed.setIsOccupied(true);
        testBed.setPatient(testPatient);

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.releaseBed("BED-001");

        assertThat(result).isNotNull();
        assertThat(result.getIsOccupied()).isFalse();
        assertThat(result.getPatient()).isNull();
        verify(bedRepository).save(testBed);
    }

    @Test
    void releaseBed_BedNotFound_ThrowsException() {
        when(bedRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.releaseBed("BED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");

        verify(bedRepository, never()).save(any());
    }

    @Test
    void releaseBed_BedNotOccupied_ThrowsException() {
        testBed.setIsOccupied(false);

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));

        assertThatThrownBy(() -> bedService.releaseBed("BED-001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed is not occupied");

        verify(bedRepository, never()).save(any());
    }

    @Test
    void releaseBedByPatient_Success() {
        testBed.setIsOccupied(true);
        testBed.setPatient(testPatient);

        when(bedRepository.findByPatientId("PAT-001"))
                .thenReturn(Optional.of(testBed));
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.releaseBedByPatient("PAT-001");

        assertThat(result).isNotNull();
        assertThat(result.getIsOccupied()).isFalse();
        assertThat(result.getPatient()).isNull();
        verify(bedRepository).save(testBed);
    }

    @Test
    void releaseBedByPatient_NoBedAssigned_ThrowsException() {
        when(bedRepository.findByPatientId(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.releaseBedByPatient("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No bed assigned to this patient");

        verify(bedRepository, never()).save(any());
    }

    // Get Available Beds Tests
    @Test
    void getAvailableBeds_Success() {
        List<Bed> availableBeds = Arrays.asList(testBed, new Bed());
        
        when(bedRepository.findByIsOccupied(false))
                .thenReturn(availableBeds);

        List<Bed> result = bedService.getAvailableBeds();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(availableBeds);
    }

    @Test
    void getAvailableBeds_NoAvailableBeds_ReturnsEmptyList() {
        when(bedRepository.findByIsOccupied(false))
                .thenReturn(Collections.emptyList());

        List<Bed> result = bedService.getAvailableBeds();

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailableBedsByDepartment_Success() {
        List<Bed> departmentBeds = Arrays.asList(testBed);
        
        when(bedRepository.findAvailableBedsByDepartment("DEPT-001"))
                .thenReturn(departmentBeds);

        List<Bed> result = bedService.getAvailableBedsByDepartment("DEPT-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testBed);
    }

    @Test
    void getAvailableBedsByDepartment_NoBeds_ReturnsEmptyList() {
        when(bedRepository.findAvailableBedsByDepartment(anyString()))
                .thenReturn(Collections.emptyList());

        List<Bed> result = bedService.getAvailableBedsByDepartment("DEPT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void countAvailableBedsByDepartment_Success() {
        when(bedRepository.countAvailableBedsByDepartment("DEPT-001"))
                .thenReturn(5L);

        Long count = bedService.countAvailableBedsByDepartment("DEPT-001");

        assertThat(count).isEqualTo(5L);
    }

    @Test
    void countAvailableBedsByDepartment_NoBeds_ReturnsZero() {
        when(bedRepository.countAvailableBedsByDepartment(anyString()))
                .thenReturn(0L);

        Long count = bedService.countAvailableBedsByDepartment("DEPT-999");

        assertThat(count).isEqualTo(0L);
    }

    // Get Bed Tests
    @Test
    void getBedById_Success() {
        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));

        Bed result = bedService.getBedById("BED-001");

        assertThat(result).isNotNull();
        assertThat(result.getBedId()).isEqualTo("BED-001");
    }

    @Test
    void getBedById_NotFound_ThrowsException() {
        when(bedRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.getBedById("BED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");
    }

    @Test
    void getAllBeds_Success() {
        List<Bed> allBeds = Arrays.asList(testBed, new Bed());
        
        when(bedRepository.findAll())
                .thenReturn(allBeds);

        List<Bed> result = bedService.getAllBeds();

        assertThat(result).hasSize(2);
    }

    @Test
    void getBedsByDepartment_Success() {
        List<Bed> departmentBeds = Arrays.asList(testBed);
        
        when(bedRepository.findByDepartmentId("DEPT-001"))
                .thenReturn(departmentBeds);

        List<Bed> result = bedService.getBedsByDepartment("DEPT-001");

        assertThat(result).hasSize(1);
    }

    @Test
    void getOccupiedBeds_Success() {
        testBed.setIsOccupied(true);
        List<Bed> occupiedBeds = Arrays.asList(testBed);
        
        when(bedRepository.findByIsOccupied(true))
                .thenReturn(occupiedBeds);

        List<Bed> result = bedService.getOccupiedBeds();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsOccupied()).isTrue();
    }

    @Test
    void getBedByPatient_Success() {
        when(bedRepository.findByPatientId("PAT-001"))
                .thenReturn(Optional.of(testBed));

        Bed result = bedService.getBedByPatient("PAT-001");

        assertThat(result).isNotNull();
    }

    @Test
    void getBedByPatient_NoBedAssigned_ThrowsException() {
        when(bedRepository.findByPatientId(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.getBedByPatient("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No bed assigned to this patient");
    }

    // Update Bed Tests
    @Test
    void updateBed_UpdateDepartment_Success() {
        Department newDepartment = new Department();
        newDepartment.setDepartmentId("DEPT-002");
        
        Bed updateData = new Bed();
        updateData.setDepartment(newDepartment);

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.updateBed("BED-001", updateData);

        assertThat(result).isNotNull();
        verify(bedRepository).save(testBed);
    }

    @Test
    void updateBed_UpdateDailyRate_Success() {
        Bed updateData = new Bed();
        updateData.setDailyRate(new BigDecimal("750.00"));

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.updateBed("BED-001", updateData);

        assertThat(result).isNotNull();
        verify(bedRepository).save(testBed);
    }

    @Test
    void updateBed_NullFields_DoesNotUpdate() {
        Bed updateData = new Bed();

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        when(bedRepository.save(any(Bed.class)))
                .thenReturn(testBed);

        Bed result = bedService.updateBed("BED-001", updateData);

        assertThat(result).isNotNull();
        verify(bedRepository).save(testBed);
    }

    @Test
    void updateBed_BedNotFound_ThrowsException() {
        when(bedRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.updateBed("BED-999", new Bed()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");

        verify(bedRepository, never()).save(any());
    }

    // Delete Bed Tests
    @Test
    void deleteBed_UnoccupiedBed_Success() {
        testBed.setIsOccupied(false);

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));
        doNothing().when(bedRepository).deleteById("BED-001");

        bedService.deleteBed("BED-001");

        verify(bedRepository).deleteById("BED-001");
    }

    @Test
    void deleteBed_OccupiedBed_ThrowsException() {
        testBed.setIsOccupied(true);

        when(bedRepository.findById("BED-001"))
                .thenReturn(Optional.of(testBed));

        assertThatThrownBy(() -> bedService.deleteBed("BED-001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot delete occupied bed");

        verify(bedRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteBed_BedNotFound_ThrowsException() {
        when(bedRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bedService.deleteBed("BED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");

        verify(bedRepository, never()).deleteById(anyString());
    }
}