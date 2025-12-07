package com.v322.healthsync.service;

import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.repository.ReceptionistRepository;
import com.v322.healthsync.repository.PharmacistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ReceptionistService {

    @Autowired
    private ReceptionistRepository receptionistRepository;

    public Receptionist createReceptionist(Receptionist receptionist) {
        return receptionistRepository.save(receptionist);
    }

    public Receptionist getReceptionistById(String receptionistId) {
        return receptionistRepository.findById(receptionistId)
            .orElseThrow(() -> new RuntimeException("Receptionist not found"));
    }

    public Receptionist getReceptionistByEmail(String email) {
        return receptionistRepository.findByEmail(email);
    }

    public Receptionist getReceptionistByContactNumber(String contactNumber) {
        return receptionistRepository.findByContactNumber(contactNumber);
    }

    public List<Receptionist> getAllReceptionists() {
        return receptionistRepository.findAll();
    }

    public Receptionist updateReceptionist(String receptionistId, Receptionist updatedReceptionist) {
        Receptionist existingReceptionist = getReceptionistById(receptionistId);

        if (updatedReceptionist.getFirstName() != null) {
            existingReceptionist.setFirstName(updatedReceptionist.getFirstName());
        }
        if (updatedReceptionist.getLastName() != null) {
            existingReceptionist.setLastName(updatedReceptionist.getLastName());
        }
        if (updatedReceptionist.getContactNumber() != null) {
            existingReceptionist.setContactNumber(updatedReceptionist.getContactNumber());
        }
        if (updatedReceptionist.getEmail() != null) {
            existingReceptionist.setEmail(updatedReceptionist.getEmail());
        }
        if (updatedReceptionist.getCity() != null) {
            existingReceptionist.setCity(updatedReceptionist.getCity());
        }

        return receptionistRepository.save(existingReceptionist);
    }

    public void deleteReceptionist(String receptionistId) {
        receptionistRepository.deleteById(receptionistId);
    }
}
