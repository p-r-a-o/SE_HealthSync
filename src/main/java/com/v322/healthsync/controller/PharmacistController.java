package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Receptionist;
import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.service.ReceptionistService;
import com.v322.healthsync.service.PharmacistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacists")
@CrossOrigin(origins = "*")
class PharmacistController {

    @Autowired
    private PharmacistService pharmacistService;

    @Autowired
    private EntityMapper entityMapper;
    @PostMapping
    public ResponseEntity<PharmacistDTO> createPharmacist(@RequestBody PharmacistDTO pharmacist) {
        try {
            Pharmacist createdPharmacist = pharmacistService.createPharmacist(entityMapper.toPharmacistEntity(pharmacist));
            return new ResponseEntity<>(DTOMapper.toPharmacistDTO(createdPharmacist), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{pharmacistId}")
    public ResponseEntity<PharmacistDTO> getPharmacistById(@PathVariable String pharmacistId) {
        try {
            Pharmacist pharmacist = pharmacistService.getPharmacistById(pharmacistId);
            return new ResponseEntity<>(DTOMapper.toPharmacistDTO(pharmacist), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PharmacistDTO> getPharmacistByEmail(@PathVariable String email) {
        try {
            Pharmacist pharmacist = pharmacistService.getPharmacistByEmail(email);
            return new ResponseEntity<>(DTOMapper.toPharmacistDTO(pharmacist), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<PharmacistDTO> getPharmacistByPharmacy(@PathVariable String pharmacyId) {
        try {
            Pharmacist pharmacist = pharmacistService.getPharmacistByPharmacy(pharmacyId);
            return new ResponseEntity<>(DTOMapper.toPharmacistDTO(pharmacist), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<PharmacistDTO>> getAllPharmacists() {
        try {
            List<Pharmacist> pharmacists = pharmacistService.getAllPharmacists();
            List<PharmacistDTO> responseList = pharmacists.stream().map(DTOMapper::toPharmacistDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{pharmacistId}")
    public ResponseEntity<PharmacistDTO> updatePharmacist(
            @PathVariable String pharmacistId,
            @RequestBody PharmacistDTO pharmacist) {
        try {
            Pharmacist updatedPharmacist = pharmacistService.updatePharmacist(pharmacistId, entityMapper.toPharmacistEntity(pharmacist));
            return new ResponseEntity<>(DTOMapper.toPharmacistDTO(updatedPharmacist), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{pharmacistId}")
    public ResponseEntity<String> deletePharmacist(@PathVariable String pharmacistId) {
        try {
            pharmacistService.deletePharmacist(pharmacistId);
            return new ResponseEntity<>("Pharmacist deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete pharmacist", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}