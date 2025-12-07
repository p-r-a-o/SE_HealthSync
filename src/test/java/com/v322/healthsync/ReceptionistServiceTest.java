package com.v322.healthsync;

import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.repository.ReceptionistRepository;
import com.v322.healthsync.service.ReceptionistService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class ReceptionistServiceTest extends BaseIntegrationTest {

    @Autowired
    ReceptionistRepository receptionistRepository;

    @Autowired
    ReceptionistService receptionistService;

    private Receptionist testReceptionist;

    @BeforeEach
    void setUp() {
        receptionistRepository.deleteAll();

        testReceptionist = new Receptionist();
        testReceptionist.setPersonId("REC-001");
        testReceptionist.setFirstName("Sarah");
        testReceptionist.setLastName("Williams");
        testReceptionist.setContactNumber("1234567890");
        testReceptionist.setEmail("sarah.williams@hospital.com");
        testReceptionist.setPassword("password");
        testReceptionist.setCity("Boston");
    }

    // Create Receptionist Tests
    @Test
    void createReceptionist_Success() {
        Receptionist result = receptionistService.createReceptionist(testReceptionist);

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("REC-001");
        assertThat(result.getFirstName()).isEqualTo("Sarah");
        assertThat(result.getLastName()).isEqualTo("Williams");
        
        Receptionist saved = receptionistRepository.findById(result.getPersonId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void createReceptionist_WithAllFields_Success() {
        Receptionist result = receptionistService.createReceptionist(testReceptionist);

        assertThat(result.getContactNumber()).isEqualTo("1234567890");
        assertThat(result.getEmail()).isEqualTo("sarah.williams@hospital.com");
        assertThat(result.getCity()).isEqualTo("Boston");
    }

    @Test
    void createReceptionist_WithMinimalFields_Success() {
        Receptionist minimal = new Receptionist();
        minimal.setPersonId("REC-002");
        minimal.setFirstName("John");
        minimal.setLastName("Doe");
        minimal.setEmail("john.doe@test.com");
        minimal.setPassword("password");

        Receptionist result = receptionistService.createReceptionist(minimal);

        assertThat(result).isNotNull();
    }

    // Get Receptionist Tests
    @Test
    void getReceptionistById_Success() {
        receptionistRepository.save(testReceptionist);

        Receptionist result = receptionistService.getReceptionistById("REC-001");

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("REC-001");
        assertThat(result.getFirstName()).isEqualTo("Sarah");
    }

    @Test
    void getReceptionistById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> receptionistService.getReceptionistById("REC-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Receptionist not found");
    }

    @Test
    void getReceptionistById_NullId_ThrowsException() {
        assertThatThrownBy(() -> receptionistService.getReceptionistById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Receptionist not found");
    }

    @Test
    void getReceptionistByEmail_Success() {
        receptionistRepository.save(testReceptionist);

        Receptionist result = receptionistService.getReceptionistByEmail("sarah.williams@hospital.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("sarah.williams@hospital.com");
    }

    @Test
    void getReceptionistByEmail_NotFound_ReturnsNull() {
        Receptionist result = receptionistService.getReceptionistByEmail("nonexistent@hospital.com");

        assertThat(result).isNull();
    }

    @Test
    void getReceptionistByEmail_CaseSensitive_ReturnsNull() {
        receptionistRepository.save(testReceptionist);

        Receptionist result = receptionistService.getReceptionistByEmail("SARAH.WILLIAMS@HOSPITAL.COM");

        assertThat(result).isNull();
    }

    @Test
    void getReceptionistByContactNumber_Success() {
        receptionistRepository.save(testReceptionist);

        Receptionist result = receptionistService.getReceptionistByContactNumber("1234567890");

        assertThat(result).isNotNull();
        assertThat(result.getContactNumber()).isEqualTo("1234567890");
    }

    @Test
    void getReceptionistByContactNumber_NotFound_ReturnsNull() {
        Receptionist result = receptionistService.getReceptionistByContactNumber("9999999999");

        assertThat(result).isNull();
    }

    @Test
    void getAllReceptionists_Success() {
        receptionistRepository.save(testReceptionist);
        
        Receptionist receptionist2 = new Receptionist();
        receptionist2.setPersonId("REC-002");
        receptionist2.setFirstName("Mike");
        receptionist2.setLastName("Johnson");
        receptionist2.setEmail("mike.johnson@hospital.com");
        receptionist2.setPassword("password");
        receptionistRepository.save(receptionist2);

        List<Receptionist> result = receptionistService.getAllReceptionists();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllReceptionists_NoReceptionists_ReturnsEmptyList() {
        List<Receptionist> result = receptionistService.getAllReceptionists();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllReceptionists_SingleReceptionist_Success() {
        receptionistRepository.save(testReceptionist);

        List<Receptionist> result = receptionistService.getAllReceptionists();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testReceptionist);
    }

    // Update Receptionist Tests
    @Test
    void updateReceptionist_AllFields_Success() {
        Receptionist saved = receptionistRepository.save(testReceptionist);
        
        Receptionist updateData = new Receptionist();
        updateData.setFirstName("Jane");
        updateData.setLastName("Doe");
        updateData.setContactNumber("9876543210");
        updateData.setEmail("jane.doe@hospital.com");
        updateData.setCity("New York");

        Receptionist result = receptionistService.updateReceptionist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getCity()).isEqualTo("New York");
    }

    @Test
    void updateReceptionist_PartialUpdate_Success() {
        Receptionist saved = receptionistRepository.save(testReceptionist);
        
        Receptionist updateData = new Receptionist();
        updateData.setFirstName("Jane");
        updateData.setLastName("Doe");

        Receptionist result = receptionistService.updateReceptionist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    void updateReceptionist_OnlyEmail_Success() {
        Receptionist saved = receptionistRepository.save(testReceptionist);
        
        Receptionist updateData = new Receptionist();
        updateData.setEmail("newemail@hospital.com");

        Receptionist result = receptionistService.updateReceptionist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("newemail@hospital.com");
    }

    @Test
    void updateReceptionist_OnlyContactNumber_Success() {
        Receptionist saved = receptionistRepository.save(testReceptionist);
        
        Receptionist updateData = new Receptionist();
        updateData.setContactNumber("5555555555");

        Receptionist result = receptionistService.updateReceptionist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getContactNumber()).isEqualTo("5555555555");
    }

    @Test
    void updateReceptionist_OnlyCity_Success() {
        Receptionist saved = receptionistRepository.save(testReceptionist);
        
        Receptionist updateData = new Receptionist();
        updateData.setCity("Chicago");

        Receptionist result = receptionistService.updateReceptionist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getCity()).isEqualTo("Chicago");
    }

    @Test
    void updateReceptionist_NullFields_DoesNotUpdate() {
        Receptionist saved = receptionistRepository.save(testReceptionist);
        String originalFirstName = saved.getFirstName();
        
        Receptionist updateData = new Receptionist();

        Receptionist result = receptionistService.updateReceptionist(saved.getPersonId(), updateData);

        assertThat(result.getFirstName()).isEqualTo(originalFirstName);
    }

    @Test
    void updateReceptionist_ReceptionistNotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                receptionistService.updateReceptionist("REC-999", new Receptionist()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Receptionist not found");
    }

    // Delete Receptionist Tests
    @Test
    void deleteReceptionist_Success() {
        Receptionist saved = receptionistRepository.save(testReceptionist);

        receptionistService.deleteReceptionist(saved.getPersonId());

        assertThat(receptionistRepository.findById(saved.getPersonId())).isEmpty();
    }

    @Test
    void deleteReceptionist_NonExistent_NoException() {
        receptionistService.deleteReceptionist("REC-999");

        // No exception thrown
    }

    @Test
    void deleteReceptionist_NullId_NoException() {
        receptionistService.deleteReceptionist(null);

        // No exception thrown
    }

    @Test
    void deleteReceptionist_MultipleDeletes_Success() {
        Receptionist rec1 = receptionistRepository.save(testReceptionist);
        
        Receptionist rec2 = new Receptionist();
        rec2.setPersonId("REC-002");
        rec2.setEmail("rec2@test.com");
        rec2.setPassword("password");
        rec2 = receptionistRepository.save(rec2);
        
        Receptionist rec3 = new Receptionist();
        rec3.setPersonId("REC-003");
        rec3.setEmail("rec3@test.com");
        rec3.setPassword("password");
        rec3 = receptionistRepository.save(rec3);

        receptionistService.deleteReceptionist(rec1.getPersonId());
        receptionistService.deleteReceptionist(rec2.getPersonId());
        receptionistService.deleteReceptionist(rec3.getPersonId());

        assertThat(receptionistRepository.count()).isEqualTo(0);
    }
}