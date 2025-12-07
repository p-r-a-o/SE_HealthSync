package com.v322.healthsync;

import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.entity.Pharmacy;
import com.v322.healthsync.repository.PharmacistRepository;
import com.v322.healthsync.service.PharmacistService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PharmacistServiceTest {

    @Mock
    private PharmacistRepository pharmacistRepository;

    @InjectMocks
    private PharmacistService pharmacistService;

    private Pharmacist testPharmacist;
    private Pharmacy testPharmacy;

    @BeforeEach
    void setUp() {
        testPharmacy = new Pharmacy();
        testPharmacy.setPharmacyId("PHAR-001");
        testPharmacy.setLocation("Main Building");

        testPharmacist = new Pharmacist();
        testPharmacist.setPersonId("PHARM-001");
        testPharmacist.setFirstName("Alice");
        testPharmacist.setLastName("Johnson");
        testPharmacist.setContactNumber("1234567890");
        testPharmacist.setEmail("alice.johnson@pharmacy.com");
        testPharmacist.setPharmacy(testPharmacy);
    }

    // Create Pharmacist Tests
    @Test
    void createPharmacist_Success() {
        when(pharmacistRepository.save(any(Pharmacist.class)))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.createPharmacist(testPharmacist);

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("PHARM-001");
        assertThat(result.getFirstName()).isEqualTo("Alice");
        verify(pharmacistRepository).save(testPharmacist);
    }

    @Test
    void createPharmacist_WithPharmacy_Success() {
        when(pharmacistRepository.save(any(Pharmacist.class)))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.createPharmacist(testPharmacist);

        assertThat(result.getPharmacy()).isNotNull();
        assertThat(result.getPharmacy().getPharmacyId()).isEqualTo("PHAR-001");
    }

    // Get Pharmacist Tests
    @Test
    void getPharmacistById_Success() {
        when(pharmacistRepository.findById("PHARM-001"))
                .thenReturn(Optional.of(testPharmacist));

        Pharmacist result = pharmacistService.getPharmacistById("PHARM-001");

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("PHARM-001");
    }

    @Test
    void getPharmacistById_NotFound_ThrowsException() {
        when(pharmacistRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pharmacistService.getPharmacistById("PHARM-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pharmacist not found");
    }

    @Test
    void getPharmacistByEmail_Success() {
        when(pharmacistRepository.findByEmail("alice.johnson@pharmacy.com"))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.getPharmacistByEmail("alice.johnson@pharmacy.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("alice.johnson@pharmacy.com");
    }

    @Test
    void getPharmacistByEmail_NotFound_ReturnsNull() {
        when(pharmacistRepository.findByEmail(anyString()))
                .thenReturn(null);

        Pharmacist result = pharmacistService.getPharmacistByEmail("nonexistent@pharmacy.com");

        assertThat(result).isNull();
    }

    @Test
    void getPharmacistByPharmacy_Success() {
        when(pharmacistRepository.findByPharmacyId("PHAR-001"))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.getPharmacistByPharmacy("PHAR-001");

        assertThat(result).isNotNull();
        assertThat(result.getPharmacy().getPharmacyId()).isEqualTo("PHAR-001");
    }

    @Test
    void getPharmacistByPharmacy_NotFound_ReturnsNull() {
        when(pharmacistRepository.findByPharmacyId(anyString()))
                .thenReturn(null);

        Pharmacist result = pharmacistService.getPharmacistByPharmacy("PHAR-999");

        assertThat(result).isNull();
    }

    @Test
    void getAllPharmacists_Success() {
        Pharmacist pharmacist2 = new Pharmacist();
        pharmacist2.setPersonId("PHARM-002");
        
        List<Pharmacist> pharmacists = Arrays.asList(testPharmacist, pharmacist2);
        when(pharmacistRepository.findAll())
                .thenReturn(pharmacists);

        List<Pharmacist> result = pharmacistService.getAllPharmacists();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testPharmacist, pharmacist2);
    }

    @Test
    void getAllPharmacists_NoPharmacists_ReturnsEmptyList() {
        when(pharmacistRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Pharmacist> result = pharmacistService.getAllPharmacists();

        assertThat(result).isEmpty();
    }

    // Update Pharmacist Tests
    @Test
    void updatePharmacist_AllFields_Success() {
        Pharmacist updateData = new Pharmacist();
        updateData.setFirstName("Bob");
        updateData.setLastName("Smith");
        updateData.setContactNumber("9876543210");
        updateData.setEmail("bob.smith@pharmacy.com");
        
        Pharmacy newPharmacy = new Pharmacy();
        newPharmacy.setPharmacyId("PHAR-002");
        updateData.setPharmacy(newPharmacy);

        when(pharmacistRepository.findById("PHARM-001"))
                .thenReturn(Optional.of(testPharmacist));
        when(pharmacistRepository.save(any(Pharmacist.class)))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.updatePharmacist("PHARM-001", updateData);

        assertThat(result).isNotNull();
        verify(pharmacistRepository).save(testPharmacist);
    }

    @Test
    void updatePharmacist_PartialUpdate_Success() {
        Pharmacist updateData = new Pharmacist();
        updateData.setFirstName("Bob");

        when(pharmacistRepository.findById("PHARM-001"))
                .thenReturn(Optional.of(testPharmacist));
        when(pharmacistRepository.save(any(Pharmacist.class)))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.updatePharmacist("PHARM-001", updateData);

        assertThat(result).isNotNull();
        verify(pharmacistRepository).save(testPharmacist);
    }

    @Test
    void updatePharmacist_NullFields_DoesNotUpdate() {
        Pharmacist updateData = new Pharmacist();

        when(pharmacistRepository.findById("PHARM-001"))
                .thenReturn(Optional.of(testPharmacist));
        when(pharmacistRepository.save(any(Pharmacist.class)))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.updatePharmacist("PHARM-001", updateData);

        assertThat(result).isNotNull();
        verify(pharmacistRepository).save(testPharmacist);
    }

    @Test
    void updatePharmacist_NotFound_ThrowsException() {
        when(pharmacistRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                pharmacistService.updatePharmacist("PHARM-999", new Pharmacist()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pharmacist not found");
    }

    @Test
    void updatePharmacist_UpdatePharmacy_Success() {
        Pharmacy newPharmacy = new Pharmacy();
        newPharmacy.setPharmacyId("PHAR-002");
        
        Pharmacist updateData = new Pharmacist();
        updateData.setPharmacy(newPharmacy);

        when(pharmacistRepository.findById("PHARM-001"))
                .thenReturn(Optional.of(testPharmacist));
        when(pharmacistRepository.save(any(Pharmacist.class)))
                .thenReturn(testPharmacist);

        Pharmacist result = pharmacistService.updatePharmacist("PHARM-001", updateData);

        assertThat(result).isNotNull();
        verify(pharmacistRepository).save(testPharmacist);
    }

    // Delete Pharmacist Tests
    @Test
    void deletePharmacist_Success() {
        doNothing().when(pharmacistRepository).deleteById("PHARM-001");

        pharmacistService.deletePharmacist("PHARM-001");

        verify(pharmacistRepository).deleteById("PHARM-001");
    }

    @Test
    void deletePharmacist_NonExistent_NoException() {
        doNothing().when(pharmacistRepository).deleteById("PHARM-999");

        pharmacistService.deletePharmacist("PHARM-999");

        verify(pharmacistRepository).deleteById("PHARM-999");
    }

    @Test
    void deletePharmacist_MultipleDeletes_Success() {
        doNothing().when(pharmacistRepository).deleteById(anyString());

        pharmacistService.deletePharmacist("PHARM-001");
        pharmacistService.deletePharmacist("PHARM-002");

        verify(pharmacistRepository, times(2)).deleteById(anyString());
    }
}