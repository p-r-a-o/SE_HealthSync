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
public class PharmacistService {

    @Autowired
    private PharmacistRepository pharmacistRepository;

    public Pharmacist createPharmacist(Pharmacist pharmacist) {
        return pharmacistRepository.save(pharmacist);
    }

    public Pharmacist getPharmacistById(String pharmacistId) {
        return pharmacistRepository.findById(pharmacistId)
            .orElseThrow(() -> new RuntimeException("Pharmacist not found"));
    }

    public Pharmacist getPharmacistByEmail(String email) {
        return pharmacistRepository.findByEmail(email);
    }

    public Pharmacist getPharmacistByPharmacy(String pharmacyId) {
        return pharmacistRepository.findByPharmacyId(pharmacyId);
    }

    public List<Pharmacist> getAllPharmacists() {
        return pharmacistRepository.findAll();
    }

    public Pharmacist updatePharmacist(String pharmacistId, Pharmacist updatedPharmacist) {
        Pharmacist existingPharmacist = getPharmacistById(pharmacistId);

        if (updatedPharmacist.getFirstName() != null) {
            existingPharmacist.setFirstName(updatedPharmacist.getFirstName());
        }
        if (updatedPharmacist.getLastName() != null) {
            existingPharmacist.setLastName(updatedPharmacist.getLastName());
        }
        if (updatedPharmacist.getContactNumber() != null) {
            existingPharmacist.setContactNumber(updatedPharmacist.getContactNumber());
        }
        if (updatedPharmacist.getEmail() != null) {
            existingPharmacist.setEmail(updatedPharmacist.getEmail());
        }
        if (updatedPharmacist.getPharmacy() != null) {
            existingPharmacist.setPharmacy(updatedPharmacist.getPharmacy());
        }

        return pharmacistRepository.save(existingPharmacist);
    }

    public void deletePharmacist(String pharmacistId) {
        pharmacistRepository.deleteById(pharmacistId);
    }
}