package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Medication;
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
@RequestMapping("/api/medications")
@CrossOrigin(origins = "*")
public class MedicationController {

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private EntityMapper entityMapper;

    // FR-14: Manage Medication Inventory - Add
    @PostMapping
    public ResponseEntity<MedicationDTO> addMedication(@RequestBody MedicationDTO medication) {
        try {
            Medication createdMedication = medicationService.addMedication(entityMapper.toMedicationEntity(medication));
            return new ResponseEntity<>(DTOMapper.toMedicationDTO(createdMedication), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-14: Manage Medication Inventory - Update
    @PutMapping("/{medicationId}")
    public ResponseEntity<MedicationDTO> updateMedication(
            @PathVariable String medicationId,
            @RequestBody MedicationDTO medication) {
        try {
            Medication updatedMedication = medicationService.updateMedication(medicationId, entityMapper.toMedicationEntity(medication));
            return new ResponseEntity<>(DTOMapper.toMedicationDTO(updatedMedication), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-14: Manage Medication Inventory - Remove
    @DeleteMapping("/{medicationId}")
    public ResponseEntity<String> deleteMedication(@PathVariable String medicationId) {
        try {
            medicationService.deleteMedication(medicationId);
            return new ResponseEntity<>("Medication deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete medication", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-15: Check Medication Availability
    @GetMapping("/{medicationId}")
    public ResponseEntity<MedicationDTO> getMedicationById(@PathVariable String medicationId) {
        try {
            Medication medication = medicationService.getMedicationById(medicationId);
            return new ResponseEntity<>(DTOMapper.toMedicationDTO(medication), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<MedicationDTO> getMedicationByName(@PathVariable String name) {
        try {
            Medication medication = medicationService.getMedicationByName(name);
            return new ResponseEntity<>(DTOMapper.toMedicationDTO(medication), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<MedicationDTO>> getAllMedications() {
        try {
            List<Medication> medications = medicationService.getAllMedications();
            List<MedicationDTO> responseList = medications.stream().map(DTOMapper::toMedicationDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<List<MedicationDTO>> getMedicationsByPharmacy(@PathVariable String pharmacyId) {
        try {
            List<Medication> medications = medicationService.getMedicationsByPharmacy(pharmacyId);
            List<MedicationDTO> responseList = medications.stream().map(DTOMapper::toMedicationDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicationDTO>> searchMedicationsByKeyword(@RequestParam String keyword) {
        try {
            List<Medication> medications = medicationService.searchMedicationsByKeyword(keyword);
            List<MedicationDTO> responseList = medications.stream().map(DTOMapper::toMedicationDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<MedicationDTO>> getMedicationsByManufacturer(@PathVariable String manufacturer) {
        try {
            List<Medication> medications = medicationService.getMedicationsByManufacturer(manufacturer);
            List<MedicationDTO> responseList = medications.stream().map(DTOMapper::toMedicationDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<MedicationDTO>> getMedicationsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        try {
            List<Medication> medications = medicationService.getMedicationsByPriceRange(minPrice, maxPrice);
            List<MedicationDTO> responseList = medications.stream().map(DTOMapper::toMedicationDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-13: Dispense Medication
    @PostMapping("/dispense/{prescriptionId}")
    public ResponseEntity<PrescriptionDTO> dispenseMedication(@PathVariable String prescriptionId) {
        try {
            Prescription prescription = medicationService.dispenseMedication(prescriptionId);
            return new ResponseEntity<>(DTOMapper.toPrescriptionDTO(prescription), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
