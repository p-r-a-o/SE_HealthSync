package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private EntityMapper entityMapper;

    // FR-1: Patient Registration
    @PostMapping("/register")
    public ResponseEntity<PatientDTO> registerPatient(@RequestBody PatientDTO patient) {
        try {
            Patient registeredPatient = patientService.registerPatient(entityMapper.toPatientEntity(patient));
            return new ResponseEntity<>(DTOMapper.toPatientDTO(registeredPatient), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-1: Update Patient Information
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable String patientId, 
                                                 @RequestBody PatientDTO patient) {
        try {
            Patient updatedPatient = patientService.updatePatient(patientId, entityMapper.toPatientEntity(patient));
            return new ResponseEntity<>(DTOMapper.toPatientDTO(updatedPatient), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable String patientId) {
        try {
            Patient patient = patientService.getPatientById(patientId);
            return new ResponseEntity<>(DTOMapper.toPatientDTO(patient), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PatientDTO> getPatientByEmail(@PathVariable String email) {
        try {
            Patient patient = patientService.getPatientByEmail(email);
            return new ResponseEntity<>(DTOMapper.toPatientDTO(patient), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        try {
            List<Patient> patients = patientService.getAllPatients();
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientDTO>> searchPatientsByName(@RequestParam String name) {
        try {
            List<Patient> patients = patientService.searchPatientsByName(name);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<PatientDTO>> getPatientsByCity(@PathVariable String city) {
        try {
            List<Patient> patients = patientService.getPatientsByCity(city);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bloodgroup/{bloodGroup}")
    public ResponseEntity<List<PatientDTO>> getPatientsByBloodGroup(@PathVariable String bloodGroup) {
        try {
            List<Patient> patients = patientService.getPatientsByBloodGroup(bloodGroup);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-2: View Medical History
    @GetMapping("/{patientId}/medical-history")
    public ResponseEntity<PatientService.MedicalHistory> getMedicalHistory(@PathVariable String patientId) {
        try {
            PatientService.MedicalHistory history = patientService.getMedicalHistory(patientId);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<String> deletePatient(@PathVariable String patientId) {
        try {
            patientService.deletePatient(patientId);
            return new ResponseEntity<>("Patient deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete patient", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}