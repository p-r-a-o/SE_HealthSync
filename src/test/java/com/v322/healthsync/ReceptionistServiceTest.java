package com.v322.healthsync;

import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.repository.ReceptionistRepository;
import com.v322.healthsync.service.ReceptionistService;

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
class ReceptionistServiceTest {

    @Mock
    private ReceptionistRepository receptionistRepository;

    @InjectMocks
    private ReceptionistService receptionistService;

    private Receptionist testReceptionist;

    @BeforeEach
    void setUp() {
        testReceptionist = new Receptionist();
        testReceptionist.setPersonId("REC-001");
        testReceptionist.setFirstName("Sarah");
        testReceptionist.setLastName("Williams");
        testReceptionist.setContactNumber("1234567890");
        testReceptionist.setEmail("sarah.williams@hospital.com");
        testReceptionist.setCity("Boston");
    }

    // Create Receptionist Tests
    @Test
    void createReceptionist_Success() {
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.createReceptionist(testReceptionist);

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("REC-001");
        assertThat(result.getFirstName()).isEqualTo("Sarah");
        assertThat(result.getLastName()).isEqualTo("Williams");
        verify(receptionistRepository).save(testReceptionist);
    }

    @Test
    void createReceptionist_WithAllFields_Success() {
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

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

        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(minimal);

        Receptionist result = receptionistService.createReceptionist(minimal);

        assertThat(result).isNotNull();
        verify(receptionistRepository).save(minimal);
    }

    // Get Receptionist Tests
    @Test
    void getReceptionistById_Success() {
        when(receptionistRepository.findById("REC-001"))
                .thenReturn(Optional.of(testReceptionist));

        Receptionist result = receptionistService.getReceptionistById("REC-001");

        assertThat(result).isNotNull();
        assertThat(result.getPersonId()).isEqualTo("REC-001");
        assertThat(result.getFirstName()).isEqualTo("Sarah");
    }

    @Test
    void getReceptionistById_NotFound_ThrowsException() {
        when(receptionistRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> receptionistService.getReceptionistById("REC-999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Receptionist not found");
    }

    @Test
    void getReceptionistById_NullId_ThrowsException() {
        when(receptionistRepository.findById(null))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> receptionistService.getReceptionistById(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Receptionist not found");
    }

    @Test
    void getReceptionistByEmail_Success() {
        when(receptionistRepository.findByEmail("sarah.williams@hospital.com"))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.getReceptionistByEmail("sarah.williams@hospital.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("sarah.williams@hospital.com");
    }

    @Test
    void getReceptionistByEmail_NotFound_ReturnsNull() {
        when(receptionistRepository.findByEmail(anyString()))
                .thenReturn(null);

        Receptionist result = receptionistService.getReceptionistByEmail("nonexistent@hospital.com");

        assertThat(result).isNull();
    }

    @Test
    void getReceptionistByEmail_CaseSensitive_ReturnsNull() {
        when(receptionistRepository.findByEmail("SARAH.WILLIAMS@HOSPITAL.COM"))
                .thenReturn(null);

        Receptionist result = receptionistService.getReceptionistByEmail("SARAH.WILLIAMS@HOSPITAL.COM");

        assertThat(result).isNull();
    }

    @Test
    void getReceptionistByContactNumber_Success() {
        when(receptionistRepository.findByContactNumber("1234567890"))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.getReceptionistByContactNumber("1234567890");

        assertThat(result).isNotNull();
        assertThat(result.getContactNumber()).isEqualTo("1234567890");
    }

    @Test
    void getReceptionistByContactNumber_NotFound_ReturnsNull() {
        when(receptionistRepository.findByContactNumber(anyString()))
                .thenReturn(null);

        Receptionist result = receptionistService.getReceptionistByContactNumber("9999999999");

        assertThat(result).isNull();
    }

    @Test
    void getAllReceptionists_Success() {
        Receptionist receptionist2 = new Receptionist();
        receptionist2.setPersonId("REC-002");
        receptionist2.setFirstName("Mike");
        
        List<Receptionist> receptionists = Arrays.asList(testReceptionist, receptionist2);
        when(receptionistRepository.findAll())
                .thenReturn(receptionists);

        List<Receptionist> result = receptionistService.getAllReceptionists();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testReceptionist, receptionist2);
    }

    @Test
    void getAllReceptionists_NoReceptionists_ReturnsEmptyList() {
        when(receptionistRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Receptionist> result = receptionistService.getAllReceptionists();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllReceptionists_SingleReceptionist_Success() {
        when(receptionistRepository.findAll())
                .thenReturn(Collections.singletonList(testReceptionist));

        List<Receptionist> result = receptionistService.getAllReceptionists();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testReceptionist);
    }

    // Update Receptionist Tests
    @Test
    void updateReceptionist_AllFields_Success() {
        Receptionist updateData = new Receptionist();
        updateData.setFirstName("Jane");
        updateData.setLastName("Doe");
        updateData.setContactNumber("9876543210");
        updateData.setEmail("jane.doe@hospital.com");
        updateData.setCity("New York");

        when(receptionistRepository.findById("REC-001"))
                .thenReturn(Optional.of(testReceptionist));
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.updateReceptionist("REC-001", updateData);

        assertThat(result).isNotNull();
        verify(receptionistRepository).save(testReceptionist);
    }

    @Test
    void updateReceptionist_PartialUpdate_Success() {
        Receptionist updateData = new Receptionist();
        updateData.setFirstName("Jane");
        updateData.setLastName("Doe");

        when(receptionistRepository.findById("REC-001"))
                .thenReturn(Optional.of(testReceptionist));
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.updateReceptionist("REC-001", updateData);

        assertThat(result).isNotNull();
        verify(receptionistRepository).save(testReceptionist);
    }

    @Test
    void updateReceptionist_OnlyEmail_Success() {
        Receptionist updateData = new Receptionist();
        updateData.setEmail("newemail@hospital.com");

        when(receptionistRepository.findById("REC-001"))
                .thenReturn(Optional.of(testReceptionist));
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.updateReceptionist("REC-001", updateData);

        assertThat(result).isNotNull();
        verify(receptionistRepository).save(testReceptionist);
    }

    @Test
    void updateReceptionist_OnlyContactNumber_Success() {
        Receptionist updateData = new Receptionist();
        updateData.setContactNumber("5555555555");

        when(receptionistRepository.findById("REC-001"))
                .thenReturn(Optional.of(testReceptionist));
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.updateReceptionist("REC-001", updateData);

        assertThat(result).isNotNull();
        verify(receptionistRepository).save(testReceptionist);
    }

    @Test
    void updateReceptionist_OnlyCity_Success() {
        Receptionist updateData = new Receptionist();
        updateData.setCity("Chicago");

        when(receptionistRepository.findById("REC-001"))
                .thenReturn(Optional.of(testReceptionist));
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.updateReceptionist("REC-001", updateData);

        assertThat(result).isNotNull();
        verify(receptionistRepository).save(testReceptionist);
    }

    @Test
    void updateReceptionist_NullFields_DoesNotUpdate() {
        Receptionist updateData = new Receptionist();

        when(receptionistRepository.findById("REC-001"))
                .thenReturn(Optional.of(testReceptionist));
        when(receptionistRepository.save(any(Receptionist.class)))
                .thenReturn(testReceptionist);

        Receptionist result = receptionistService.updateReceptionist("REC-001", updateData);

        assertThat(result).isNotNull();
        verify(receptionistRepository).save(testReceptionist);
    }

    @Test
    void updateReceptionist_ReceptionistNotFound_ThrowsException() {
        when(receptionistRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> 
                receptionistService.updateReceptionist("REC-999", new Receptionist()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Receptionist not found");

        verify(receptionistRepository, never()).save(any());
    }

    // Delete Receptionist Tests
    @Test
    void deleteReceptionist_Success() {
        doNothing().when(receptionistRepository).deleteById("REC-001");

        receptionistService.deleteReceptionist("REC-001");

        verify(receptionistRepository).deleteById("REC-001");
    }

    @Test
    void deleteReceptionist_NonExistent_NoException() {
        doNothing().when(receptionistRepository).deleteById("REC-999");

        receptionistService.deleteReceptionist("REC-999");

        verify(receptionistRepository).deleteById("REC-999");
    }

    @Test
    void deleteReceptionist_NullId_InvokesDelete() {
        doNothing().when(receptionistRepository).deleteById(null);

        receptionistService.deleteReceptionist(null);

        verify(receptionistRepository).deleteById(null);
    }

    @Test
    void deleteReceptionist_MultipleDeletes_Success() {
        doNothing().when(receptionistRepository).deleteById(anyString());

        receptionistService.deleteReceptionist("REC-001");
        receptionistService.deleteReceptionist("REC-002");
        receptionistService.deleteReceptionist("REC-003");

        verify(receptionistRepository, times(3)).deleteById(anyString());
    }
}