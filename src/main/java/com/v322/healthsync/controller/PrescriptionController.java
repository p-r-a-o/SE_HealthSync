package com.v322.healthsync.controller;

import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.PrescriptionItem;
import com.v322.healthsync.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private EntityMapper entityMapper;

    // FR-12: Write Prescription
    @PostMapping
    public ResponseEntity<PrescriptionDTO> createPrescription(@RequestBody PrescriptionDTO prescription) {
        try {
            Prescription createdPrescription = prescriptionService.createPrescription(entityMapper.toPrescriptionEntity(prescription));
            return new ResponseEntity<>(DTOMapper.toPrescriptionDTO(createdPrescription), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/with-items")
    public ResponseEntity<PrescriptionDTO> createPrescriptionWithItems(
            @RequestBody PrescriptionRequest request) {
        try {
            Prescription prescription = prescriptionService.createPrescriptionWithItems(request.getPrescription(entityMapper), request.getItems(entityMapper));
            return new ResponseEntity<>(DTOMapper.toPrescriptionDTO(prescription), HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<PrescriptionItemDTO> addPrescriptionItem(@RequestBody PrescriptionItemDTO item) {
        try {
            PrescriptionItem createdItem = prescriptionService.addPrescriptionItem(entityMapper.toPrescriptionItemEntity(item));
            return new ResponseEntity<>(DTOMapper.toPrescriptionItemDTO(createdItem), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-12: View Prescription
    @GetMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable String prescriptionId) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
            return new ResponseEntity<>(DTOMapper.toPrescriptionDTO(prescription), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByPatient(@PathVariable String patientId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
            List<PrescriptionDTO> responseList = prescriptions.stream().map(DTOMapper::toPrescriptionDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDoctor(@PathVariable String doctorId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
            List<PrescriptionDTO> responseList = prescriptions.stream().map(DTOMapper::toPrescriptionDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByStatus(@PathVariable String status) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByStatus(status);
            List<PrescriptionDTO> responseList = prescriptions.stream().map(DTOMapper::toPrescriptionDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{prescriptionId}/items")
    public ResponseEntity<List<PrescriptionItemDTO>> getPrescriptionItems(@PathVariable String prescriptionId) {
        try {
            List<PrescriptionItem> items = prescriptionService.getPrescriptionItems(prescriptionId);
            List<PrescriptionItemDTO> responseList = items.stream().map(DTOMapper::toPrescriptionItemDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDateRange(startDate, endDate);
            List<PrescriptionDTO> responseList = prescriptions.stream().map(DTOMapper::toPrescriptionDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-13: Update Prescription Status (for dispensing)
    @PutMapping("/{prescriptionId}/status")
    public ResponseEntity<PrescriptionDTO> updatePrescriptionStatus(
            @PathVariable String prescriptionId,
            @RequestParam String status) {
        try {
            Prescription prescription = prescriptionService.updatePrescriptionStatus(prescriptionId, status);
            return new ResponseEntity<>(DTOMapper.toPrescriptionDTO(prescription), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionDTO> updatePrescription(
            @PathVariable String prescriptionId,
            @RequestBody PrescriptionDTO prescription) {
        try {
            Prescription updatedPrescription = prescriptionService.updatePrescription(prescriptionId, entityMapper.toPrescriptionEntity(prescription));
            return new ResponseEntity<>(DTOMapper.toPrescriptionDTO(updatedPrescription), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{prescriptionId}")
    public ResponseEntity<String> deletePrescription(@PathVariable String prescriptionId) {
        try {
            prescriptionService.deletePrescription(prescriptionId);
            return new ResponseEntity<>("Prescription deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete prescription", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deletePrescriptionItem(@PathVariable String itemId) {
        try {
            prescriptionService.deletePrescriptionItem(itemId);
            return new ResponseEntity<>("Prescription item deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete prescription item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper class for request body
    public static class PrescriptionRequest {
        private PrescriptionDTO prescription;
        private List<PrescriptionItemDTO> items;

        public Prescription getPrescription(EntityMapper entityMapper) { return entityMapper.toPrescriptionEntity(prescription); }
        public void setPrescription(PrescriptionDTO prescription) { this.prescription = prescription; }
        public List<PrescriptionItem> getItems(EntityMapper entityMapper) { List<PrescriptionItem> requestList = items.stream().map(entityMapper::toPrescriptionItemEntity).collect(Collectors.toList()); return requestList; }
        public void setItems(List<PrescriptionItemDTO> items) { this.items = items; }
    }
}