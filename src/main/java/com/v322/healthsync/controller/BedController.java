package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Bed;
import com.v322.healthsync.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "*")
public class BedController {

    @Autowired
    private BedService bedService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<BedDTO> createBed(@RequestBody BedDTO bed) {
        try {
            Bed createdBed = bedService.createBed(entityMapper.toBedEntity(bed));
            return new ResponseEntity<>(DTOMapper.toBedDTO(createdBed), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-16: Assign Bed to Patient
    @PostMapping("/{bedId}/assign")
    public ResponseEntity<BedDTO> assignBedToPatient(
            @PathVariable String bedId,
            @RequestParam String patientId) {
        try {
            Bed bed = bedService.assignBedToPatient(bedId, patientId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-16: Release Bed from Patient
    @PostMapping("/{bedId}/release")
    public ResponseEntity<BedDTO> releaseBed(@PathVariable String bedId) {
        try {
            Bed bed = bedService.releaseBed(bedId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/patient/{patientId}/release")
    public ResponseEntity<BedDTO> releaseBedByPatient(@PathVariable String patientId) {
        try {
            Bed bed = bedService.releaseBedByPatient(patientId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-17: Check Bed Availability
    @GetMapping("/available")
    public ResponseEntity<List<BedDTO>> getAvailableBeds() {
        try {
            List<Bed> beds = bedService.getAvailableBeds();
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available/department/{departmentId}")
    public ResponseEntity<List<BedDTO>> getAvailableBedsByDepartment(@PathVariable String departmentId) {
        try {
            List<Bed> beds = bedService.getAvailableBedsByDepartment(departmentId);
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/available/count/department/{departmentId}")
    public ResponseEntity<Long> countAvailableBedsByDepartment(@PathVariable String departmentId) {
        try {
            Long count = bedService.countAvailableBedsByDepartment(departmentId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{bedId}")
    public ResponseEntity<BedDTO> getBedById(@PathVariable String bedId) {
        try {
            Bed bed = bedService.getBedById(bedId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<BedDTO>> getAllBeds() {
        try {
            List<Bed> beds = bedService.getAllBeds();
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<BedDTO>> getBedsByDepartment(@PathVariable String departmentId) {
        try {
            List<Bed> beds = bedService.getBedsByDepartment(departmentId);
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/occupied")
    public ResponseEntity<List<BedDTO>> getOccupiedBeds() {
        try {
            List<Bed> beds = bedService.getOccupiedBeds();
            List<BedDTO> responseList = beds.stream().map(DTOMapper::toBedDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<BedDTO> getBedByPatient(@PathVariable String patientId) {
        try {
            Bed bed = bedService.getBedByPatient(patientId);
            return new ResponseEntity<>(DTOMapper.toBedDTO(bed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{bedId}")
    public ResponseEntity<BedDTO> updateBed(@PathVariable String bedId, @RequestBody BedDTO bed) {
        try {
            Bed updatedBed = bedService.updateBed(bedId, entityMapper.toBedEntity(bed));
            return new ResponseEntity<>(DTOMapper.toBedDTO(updatedBed), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{bedId}")
    public ResponseEntity<String> deleteBed(@PathVariable String bedId) {
        try {
            bedService.deleteBed(bedId);
            return new ResponseEntity<>("Bed deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete bed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}