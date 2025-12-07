package com.v322.healthsync.controller;

import com.v322.healthsync.entity.Medication;
import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Pharmacy;
import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.service.MedicationService;
import com.v322.healthsync.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@CrossOrigin(origins = "*")
class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<PharmacyDTO> createPharmacy(@RequestBody PharmacyDTO pharmacy) {
        try {
            Pharmacy createdPharmacy = pharmacyService.createPharmacy(entityMapper.toPharmacyEntity(pharmacy));
            return new ResponseEntity<>(DTOMapper.toPharmacyDTO(createdPharmacy), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{pharmacyId}")
    public ResponseEntity<PharmacyDTO> getPharmacyById(@PathVariable String pharmacyId) {
        try {
            Pharmacy pharmacy = pharmacyService.getPharmacyById(pharmacyId);
            return new ResponseEntity<>(DTOMapper.toPharmacyDTO(pharmacy), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<PharmacyDTO>> getAllPharmacies() {
        try {
            List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
            List<PharmacyDTO> responseList = pharmacies.stream().map(DTOMapper::toPharmacyDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<PharmacyDTO>> getPharmaciesByLocation(@PathVariable String location) {
        try {
            List<Pharmacy> pharmacies = pharmacyService.getPharmaciesByLocation(location);
            List<PharmacyDTO> responseList = pharmacies.stream().map(DTOMapper::toPharmacyDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{pharmacyId}")
    public ResponseEntity<PharmacyDTO> updatePharmacy(
            @PathVariable String pharmacyId,
            @RequestBody PharmacyDTO pharmacy) {
        try {
            Pharmacy updatedPharmacy = pharmacyService.updatePharmacy(pharmacyId, entityMapper.toPharmacyEntity(pharmacy));
            return new ResponseEntity<>(DTOMapper.toPharmacyDTO(updatedPharmacy), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{pharmacyId}")
    public ResponseEntity<String> deletePharmacy(@PathVariable String pharmacyId) {
        try {
            pharmacyService.deletePharmacy(pharmacyId);
            return new ResponseEntity<>("Pharmacy deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete pharmacy", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}