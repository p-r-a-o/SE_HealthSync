package com.v322.healthsync;

import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.entity.Pharmacy;
import com.v322.healthsync.repository.PharmacistRepository;
import com.v322.healthsync.repository.PharmacyRepository;
import com.v322.healthsync.service.PharmacistService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class PharmacistServiceTest extends BaseIntegrationTest {

    @Autowired
    PharmacistRepository pharmacistRepository;

    @Autowired
    PharmacyRepository pharmacyRepository;

    @Autowired
    PharmacistService pharmacistService;

    private Pharmacist testPharmacist;
    private Pharmacy testPharmacy;

    @BeforeEach
    void setUp() {
        pharmacistRepository.deleteAll();
        pharmacyRepository.deleteAll();

        testPharmacy = new Pharmacy();
        testPharmacy.setPharmacyId("PHAR-001");
        testPharmacy.setLocation("Main Building");
        testPharmacy = pharmacyRepository.save(testPharmacy);

        testPharmacist = new Pharmacist();
        testPharmacist.setPersonId("PHARM-001");
        testPharmacist.setFirstName("Alice");
        testPharmacist.setLastName("Johnson");
        testPharmacist.setContactNumber("1234567890");
        testPharmacist.setEmail("alice.johnson@pharmacy.com");
        testPharmacist.setPassword("password");
        testPharmacist.setPharmacy(testPharmacy);
    }

    // Create Pharmacist Tests
    @Test
    void createPharmacist_Success() {
        Pharmacist result = pharmacistService.createPharmacist(testPharmacist);

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("PHARM-001");
        assertThat(result.getFirstName()).isEqualTo("Alice");
        
        Pharmacist saved = pharmacistRepository.findById(result.getPersonId()).orElse(null);
        assertThat(saved).isNotNull();
    }

    @Test
    void createPharmacist_WithPharmacy_Success() {
        Pharmacist result = pharmacistService.createPharmacist(testPharmacist);

        assertThat(result.getPharmacy()).isNotNull();
        assertThat(result.getPharmacy().getPharmacyId()).isEqualTo("PHAR-001");
    }

    // Get Pharmacist Tests
    @Test
    void getPharmacistById_Success() {
        pharmacistRepository.save(testPharmacist);

        Pharmacist result = pharmacistService.getPharmacistById("PHARM-001");

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("PHARM-001");
    }

    @Test
    void getPharmacistById_NotFound_ThrowsException() {
        assertThatThrownBy(() -> pharmacistService.getPharmacistById("PHARM-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pharmacist not found");
    }

    @Test
    void getPharmacistByEmail_Success() {
        pharmacistRepository.save(testPharmacist);

        Pharmacist result = pharmacistService.getPharmacistByEmail("alice.johnson@pharmacy.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("alice.johnson@pharmacy.com");
    }

    @Test
    void getPharmacistByEmail_NotFound_ReturnsNull() {
        Pharmacist result = pharmacistService.getPharmacistByEmail("nonexistent@pharmacy.com");

        assertThat(result).isNull();
    }

    @Test
    void getPharmacistByPharmacy_Success() {
        pharmacistRepository.save(testPharmacist);

        Pharmacist result = pharmacistService.getPharmacistByPharmacy(testPharmacy.getPharmacyId());

        assertThat(result).isNotNull();
        assertThat(result.getPharmacy().getPharmacyId()).isEqualTo("PHAR-001");
    }

    @Test
    void getPharmacistByPharmacy_NotFound_ReturnsNull() {
        Pharmacist result = pharmacistService.getPharmacistByPharmacy("PHAR-999");

        assertThat(result).isNull();
    }

    @Test
    void getAllPharmacists_Success() {
        pharmacistRepository.save(testPharmacist);
        
        Pharmacist pharmacist2 = new Pharmacist();
        pharmacist2.setPersonId("PHARM-002");
        pharmacist2.setFirstName("Bob");
        pharmacist2.setLastName("Smith");
        pharmacist2.setEmail("bob.smith@pharmacy.com");
        pharmacist2.setPassword("password");
        pharmacist2.setPharmacy(testPharmacy);
        pharmacistRepository.save(pharmacist2);

        List<Pharmacist> result = pharmacistService.getAllPharmacists();

        assertThat(result).hasSize(2);
    }

    @Test
    void getAllPharmacists_NoPharmacists_ReturnsEmptyList() {
        List<Pharmacist> result = pharmacistService.getAllPharmacists();

        assertThat(result).isEmpty();
    }

    // Update Pharmacist Tests
    @Test
    void updatePharmacist_AllFields_Success() {
        Pharmacist saved = pharmacistRepository.save(testPharmacist);
        
        Pharmacy newPharmacy = new Pharmacy();
        newPharmacy.setPharmacyId("PHAR-002");
        newPharmacy.setLocation("North Building");
        newPharmacy = pharmacyRepository.save(newPharmacy);
        
        Pharmacist updateData = new Pharmacist();
        updateData.setFirstName("Bob");
        updateData.setLastName("Smith");
        updateData.setContactNumber("9876543210");
        updateData.setEmail("bob.smith@pharmacy.com");
        updateData.setPharmacy(newPharmacy);

        Pharmacist result = pharmacistService.updatePharmacist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Bob");
        assertThat(result.getPharmacy().getPharmacyId()).isEqualTo("PHAR-002");
    }

    @Test
    void updatePharmacist_PartialUpdate_Success() {
        Pharmacist saved = pharmacistRepository.save(testPharmacist);
        
        Pharmacist updateData = new Pharmacist();
        updateData.setFirstName("Bob");

        Pharmacist result = pharmacistService.updatePharmacist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Bob");
        assertThat(result.getLastName()).isEqualTo("Johnson"); // unchanged
    }

    @Test
    void updatePharmacist_NullFields_DoesNotUpdate() {
        Pharmacist saved = pharmacistRepository.save(testPharmacist);
        String originalFirstName = saved.getFirstName();
        
        Pharmacist updateData = new Pharmacist();

        Pharmacist result = pharmacistService.updatePharmacist(saved.getPersonId(), updateData);

        assertThat(result.getFirstName()).isEqualTo(originalFirstName);
    }

    @Test
    void updatePharmacist_NotFound_ThrowsException() {
        assertThatThrownBy(() -> 
                pharmacistService.updatePharmacist("PHARM-999", new Pharmacist()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pharmacist not found");
    }

    @Test
    void updatePharmacist_UpdatePharmacy_Success() {
        Pharmacist saved = pharmacistRepository.save(testPharmacist);
        
        Pharmacy newPharmacy = new Pharmacy();
        newPharmacy.setPharmacyId("PHAR-002");
        newPharmacy.setLocation("South Building");
        newPharmacy = pharmacyRepository.save(newPharmacy);
        
        Pharmacist updateData = new Pharmacist();
        updateData.setPharmacy(newPharmacy);

        Pharmacist result = pharmacistService.updatePharmacist(saved.getPersonId(), updateData);

        assertThat(result).isNotNull();
        assertThat(result.getPharmacy().getPharmacyId()).isEqualTo("PHAR-002");
    }

    // Delete Pharmacist Tests
    @Test
    void deletePharmacist_Success() {
        Pharmacist saved = pharmacistRepository.save(testPharmacist);

        pharmacistService.deletePharmacist(saved.getPersonId());

        assertThat(pharmacistRepository.findById(saved.getPersonId())).isEmpty();
    }

    @Test
    void deletePharmacist_NonExistent_NoException() {
        pharmacistService.deletePharmacist("PHARM-999");

        // No exception thrown
    }

    @Test
    void deletePharmacist_MultipleDeletes_Success() {
        Pharmacist pharm1 = pharmacistRepository.save(testPharmacist);
        
        Pharmacist pharm2 = new Pharmacist();
        pharm2.setPersonId("PHARM-002");
        pharm2.setEmail("pharm2@test.com");
        pharm2.setPassword("password");
        pharm2 = pharmacistRepository.save(pharm2);

        pharmacistService.deletePharmacist(pharm1.getPersonId());
        pharmacistService.deletePharmacist(pharm2.getPersonId());

        assertThat(pharmacistRepository.count()).isEqualTo(0);
    }
}