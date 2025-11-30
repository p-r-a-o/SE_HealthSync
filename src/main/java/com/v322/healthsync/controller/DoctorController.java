package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Doctor;
import com.v322.healthsync.entity.DoctorAvailability;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorDTO doctor) {
        try {
            Doctor createdDoctor = doctorService.createDoctor(entityMapper.toDoctorEntity(doctor));
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(createdDoctor), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable String doctorId) {
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(doctor), HttpStatus.OK);  
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<DoctorDTO> getDoctorByEmail(@PathVariable String email) {
        try {
            Doctor doctor = doctorService.getDoctorByEmail(email);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(doctor), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByDepartment(@PathVariable String departmentId) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByDepartment(departmentId);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@PathVariable String specialization) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctorsByName(@RequestParam String name) {
        try {
            List<Doctor> doctors = doctorService.searchDoctorsByName(name);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fee")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByConsultationFee(@RequestParam BigDecimal maxFee) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByConsultationFee(maxFee);
            List<DoctorDTO> responseList = doctors.stream().map(DTOMapper::toDoctorDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-9: Manage Doctor Availability
    @PostMapping("/{doctorId}/availability")
    public ResponseEntity<DoctorAvailabilityDTO> addDoctorAvailability(
            @PathVariable String doctorId,
            @RequestBody DoctorAvailabilityDTO availability) {
        try {
            DoctorAvailability createdAvailability = doctorService.addDoctorAvailability(entityMapper.toDoctorAvailabilityEntity(availability));
            return new ResponseEntity<>(DTOMapper.toDoctorAvailabilityDTO(createdAvailability), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/availability/{slotId}")
    public ResponseEntity<DoctorAvailabilityDTO> updateDoctorAvailability(
            @PathVariable String slotId,
            @RequestBody DoctorAvailabilityDTO availability) {
        try {
            DoctorAvailability updatedAvailability = doctorService.updateDoctorAvailability(slotId, entityMapper.toDoctorAvailabilityEntity(availability));
            return new ResponseEntity<>(DTOMapper.toDoctorAvailabilityDTO(updatedAvailability), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/availability/{slotId}")
    public ResponseEntity<String> deleteDoctorAvailability(@PathVariable String slotId) {
        try {
            doctorService.deleteDoctorAvailability(slotId);
            return new ResponseEntity<>("Availability slot deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete availability slot", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getDoctorAvailability(@PathVariable String doctorId) {
        try {
            List<DoctorAvailability> availabilities = doctorService.getDoctorAvailability(doctorId);
            List<DoctorAvailabilityDTO> responseList = availabilities.stream().map(DTOMapper::toDoctorAvailabilityDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{doctorId}/availability/{dayOfWeek}")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getDoctorAvailabilityByDay(
            @PathVariable String doctorId,
            @PathVariable String dayOfWeek) {
        try {
            List<DoctorAvailability> availabilities = doctorService.getDoctorAvailabilityByDay(doctorId, dayOfWeek);
            List<DoctorAvailabilityDTO> responseList = availabilities.stream().map(DTOMapper::toDoctorAvailabilityDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-10: View Patient List
    @GetMapping("/{doctorId}/patients")
    public ResponseEntity<List<PatientDTO>> getPatientListForDoctor(@PathVariable String doctorId) {
        try {
            List<Patient> patients = doctorService.getPatientListForDoctor(doctorId);
            List<PatientDTO> responseList = patients.stream().map(DTOMapper::toPatientDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-11: Update Consultation Fee
    @PutMapping("/{doctorId}/consultation-fee")
    public ResponseEntity<DoctorDTO> updateConsultationFee(
            @PathVariable String doctorId,
            @RequestParam BigDecimal newFee) {
        try {
            Doctor doctor = doctorService.updateConsultationFee(doctorId, newFee);
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(doctor), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable String doctorId, @RequestBody DoctorDTO doctor) {
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(doctorId, entityMapper.toDoctorEntity(doctor));   
            return new ResponseEntity<>(DTOMapper.toDoctorDTO(updatedDoctor), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<String> deleteDoctor(@PathVariable String doctorId) {
        try {
            doctorService.deleteDoctor(doctorId);
            return new ResponseEntity<>("Doctor deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete doctor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}