package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PharmacyService {

    @Autowired
    private PharmacyRepository pharmacyRepository;
    
    @Autowired
    private PharmacistRepository pharmacistRepository;

    public Pharmacy createPharmacy(Pharmacy pharmacy) {
        pharmacy.setPharmacyId("PHAR-" + UUID.randomUUID().toString());
        return pharmacyRepository.save(pharmacy);
    }

    public Pharmacy getPharmacyById(String pharmacyId) {
        return pharmacyRepository.findById(pharmacyId)
            .orElseThrow(() -> new RuntimeException("Pharmacy not found"));
    }

    public List<Pharmacy> getAllPharmacies() {
        return pharmacyRepository.findAll();
    }

    public List<Pharmacy> getPharmaciesByLocation(String location) {
        return pharmacyRepository.findByLocation(location);
    }

    public Pharmacy updatePharmacy(String pharmacyId, Pharmacy updatedPharmacy) {
        Pharmacy existingPharmacy = getPharmacyById(pharmacyId);

        if (updatedPharmacy.getLocation() != null) {
            existingPharmacy.setLocation(updatedPharmacy.getLocation());
        }

        return pharmacyRepository.save(existingPharmacy);
    }

    public void deletePharmacy(String pharmacyId) {
        pharmacyRepository.deleteById(pharmacyId);
    }
}