package com.v322.healthsync;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import com.v322.healthsync.service.BedService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class BedServiceTest extends BaseIntegrationTest {

    @Autowired
    BedRepository bedRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    BedService bedService;

    private Bed testBed;
    private Patient testPatient;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        bedRepository.deleteAll();
        patientRepository.deleteAll();
        departmentRepository.deleteAll();
        
        testDepartment = new Department();
        testDepartment.setDepartmentId("DEPT-001");
        testDepartment.setName("Cardiology");
        testDepartment.setLocation("Building A");
        testDepartment = departmentRepository.save(testDepartment);

        testBed = new Bed();
        testBed.setDepartment(testDepartment);
        testBed.setDailyRate(new BigDecimal("500.00"));
        testBed.setIsOccupied(false);

        testPatient = new Patient();
        testPatient.setPersonId("PAT-001");
        testPatient.setFirstName("John");
        testPatient.setLastName("Doe");
        testPatient.setEmail("john.doe@test.com");
        testPatient.setPassword("password");
        testPatient = patientRepository.save(testPatient);
    }

    // Create Bed Tests
    @Test
    void createBed_Success() {
        Bed result = bedService.createBed(testBed);

        assertThat(result).isNotNull();
        assertThat(result.getBedId()).startsWith("BED-");
        assertThat(result.getIsOccupied()).isFalse();
    }

    @Test
    void createBed_SetsOccupiedToFalse() {
        testBed.setIsOccupied(true);
        
        Bed result = bedService.createBed(testBed);

        assertThat(result.getIsOccupied()).isFalse();
    }

    @Test
    void createBed_GeneratesUniqueBedId() {
        Bed result = bedService.createBed(testBed);

        assertThat(result.getBedId()).matches("BED-[a-f0-9-]+");
    }

    // Assign Bed to Patient Tests
    @Test
    void assignBedToPatient_Success() {
        Bed saved = bedService.createBed(testBed);

        Bed result = bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        assertThat(result).isNotNull();
        assertThat(result.getIsOccupied()).isTrue();
        assertThat(result.getPatient().getPersonId()).isEqualTo(testPatient.getPersonId());
    }

    @Test
    void assignBedToPatient_BedNotFound_ThrowsException() {
        assertThatThrownBy(() -> bedService.assignBedToPatient("BED-999", "PAT-001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");
    }

    @Test
    void assignBedToPatient_BedAlreadyOccupied_ThrowsException() {
        Bed saved = bedService.createBed(testBed);
        bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        assertThatThrownBy(() -> bedService.assignBedToPatient(saved.getBedId(), "PAT-002"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed is already occupied");
    }

    @Test
    void assignBedToPatient_PatientNotFound_ThrowsException() {
        Bed saved = bedService.createBed(testBed);

        assertThatThrownBy(() -> bedService.assignBedToPatient(saved.getBedId(), "PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Patient not found");
    }

//     @Test
//     void assignBedToPatient_PatientAlreadyHasBed_ThrowsException() {
//         Bed bed1 = bedService.createBed(testBed);
//         bedService.assignBedToPatient(bed1.getBedId(), testPatient.getPersonId());

//         Bed bed2 = new Bed();
//         bed2.setDepartment(testDepartment);
//         bed2.setDailyRate(new BigDecimal("600.00"));
//         bed2 = bedService.createBed(bed2);

//         assertThatThrownBy(() -> bedService.assignBedToPatient(bed2.getBedId(), testPatient.getPersonId()))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessage("Patient already has a bed assigned");
//     }

    // Release Bed Tests
    @Test
    void releaseBed_Success() {
        Bed saved = bedService.createBed(testBed);
        bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        Bed result = bedService.releaseBed(saved.getBedId());

        assertThat(result).isNotNull();
        assertThat(result.getIsOccupied()).isFalse();
        assertThat(result.getPatient()).isNull();
    }

    @Test
    void releaseBed_BedNotFound_ThrowsException() {
        assertThatThrownBy(() -> bedService.releaseBed("BED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");
    }

    @Test
    void releaseBed_BedNotOccupied_ThrowsException() {
        Bed saved = bedService.createBed(testBed);

        assertThatThrownBy(() -> bedService.releaseBed(saved.getBedId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed is not occupied");
    }

    @Test
    void releaseBedByPatient_Success() {
        Bed saved = bedService.createBed(testBed);
        bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        Bed result = bedService.releaseBedByPatient(testPatient.getPersonId());

        assertThat(result).isNotNull();
        assertThat(result.getIsOccupied()).isFalse();
        assertThat(result.getPatient()).isNull();
    }

    @Test
    void releaseBedByPatient_NoBedAssigned_ThrowsException() {
        assertThatThrownBy(() -> bedService.releaseBedByPatient("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No bed assigned to this patient");
    }

    // Get Available Beds Tests
    @Test
    void getAvailableBeds_Success() {
        bedService.createBed(testBed);
        
        Bed bed2 = new Bed();
        bed2.setDepartment(testDepartment);
        bed2.setDailyRate(new BigDecimal("600.00"));
        bedService.createBed(bed2);

        List<Bed> result = bedService.getAvailableBeds();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAvailableBeds_NoAvailableBeds_ReturnsEmptyList() {
        Bed saved = bedService.createBed(testBed);
        bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        List<Bed> result = bedService.getAvailableBeds();

        assertThat(result).isEmpty();
    }

    @Test
    void getAvailableBedsByDepartment_Success() {
        bedService.createBed(testBed);

        List<Bed> result = bedService.getAvailableBedsByDepartment(testDepartment.getDepartmentId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getAvailableBedsByDepartment_NoBeds_ReturnsEmptyList() {
        List<Bed> result = bedService.getAvailableBedsByDepartment("DEPT-999");

        assertThat(result).isEmpty();
    }

    @Test
    void countAvailableBedsByDepartment_Success() {
        bedService.createBed(testBed);
        
        Bed bed2 = new Bed();
        bed2.setDepartment(testDepartment);
        bed2.setDailyRate(new BigDecimal("600.00"));
        bedService.createBed(bed2);

        Long count = bedService.countAvailableBedsByDepartment(testDepartment.getDepartmentId());

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void countAvailableBedsByDepartment_NoBeds_ReturnsZero() {
        Long count = bedService.countAvailableBedsByDepartment("DEPT-999");

        assertThat(count).isEqualTo(0L);
    }

    // Get Bed Tests
    @Test
    void getBedById_Success() {
        Bed saved = bedService.createBed(testBed);

        Bed result = bedService.getBedById(saved.getBedId());

        assertThat(result).isNotNull();
        assertThat(result.getBedId()).isEqualTo(saved.getBedId());
    }

    @Test
    void getBedById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> bedService.getBedById("BED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");
    }

    @Test
    void getAllBeds_Success() {
        bedService.createBed(testBed);
        
        Bed bed2 = new Bed();
        bed2.setDepartment(testDepartment);
        bed2.setDailyRate(new BigDecimal("600.00"));
        bedService.createBed(bed2);

        List<Bed> result = bedService.getAllBeds();

        assertThat(result).hasSize(2);
    }

    @Test
    void getBedsByDepartment_Success() {
        bedService.createBed(testBed);

        List<Bed> result = bedService.getBedsByDepartment(testDepartment.getDepartmentId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getOccupiedBeds_Success() {
        Bed saved = bedService.createBed(testBed);
        bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        List<Bed> result = bedService.getOccupiedBeds();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsOccupied()).isTrue();
    }

    @Test
    void getBedByPatient_Success() {
        Bed saved = bedService.createBed(testBed);
        bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        Bed result = bedService.getBedByPatient(testPatient.getPersonId());

        assertThat(result).isNotNull();
    }

    @Test
    void getBedByPatient_NoBedAssigned_ThrowsException() {
        assertThatThrownBy(() -> bedService.getBedByPatient("PAT-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No bed assigned to this patient");
    }

    // Update Bed Tests
    @Test
    void updateBed_UpdateDepartment_Success() {
        Bed saved = bedService.createBed(testBed);
        
        Department newDepartment = new Department();
        newDepartment.setDepartmentId("DEPT-002");
        newDepartment.setName("Neurology");
        newDepartment.setLocation("Building B");
        newDepartment = departmentRepository.save(newDepartment);
        
        Bed updateData = new Bed();
        updateData.setDepartment(newDepartment);

        Bed result = bedService.updateBed(saved.getBedId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getDepartment().getDepartmentId()).isEqualTo("DEPT-002");
    }

    @Test
    void updateBed_UpdateDailyRate_Success() {
        Bed saved = bedService.createBed(testBed);
        
        Bed updateData = new Bed();
        updateData.setDailyRate(new BigDecimal("750.00"));

        Bed result = bedService.updateBed(saved.getBedId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getDailyRate()).isEqualByComparingTo(new BigDecimal("750.00"));
    }

    @Test
    void updateBed_NullFields_DoesNotUpdate() {
        Bed saved = bedService.createBed(testBed);
        BigDecimal originalRate = saved.getDailyRate();
        
        Bed updateData = new Bed();

        Bed result = bedService.updateBed(saved.getBedId(), updateData);

        assertThat(result.getDailyRate()).isEqualByComparingTo(originalRate);
    }

    @Test
    void updateBed_BedNotFound_ThrowsException() {
        assertThatThrownBy(() -> bedService.updateBed("BED-999", new Bed()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");
    }

    // Delete Bed Tests
    @Test
    void deleteBed_UnoccupiedBed_Success() {
        Bed saved = bedService.createBed(testBed);

        bedService.deleteBed(saved.getBedId());

        assertThat(bedRepository.findById(saved.getBedId())).isEmpty();
    }

    @Test
    void deleteBed_OccupiedBed_ThrowsException() {
        Bed saved = bedService.createBed(testBed);
        bedService.assignBedToPatient(saved.getBedId(), testPatient.getPersonId());

        assertThatThrownBy(() -> bedService.deleteBed(saved.getBedId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot delete occupied bed");
    }

    @Test
    void deleteBed_BedNotFound_ThrowsException() {
        assertThatThrownBy(() -> bedService.deleteBed("BED-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bed not found");
    }
}
