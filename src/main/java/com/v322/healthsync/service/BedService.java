package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BedService {

    @Autowired
    private BedRepository bedRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    public Bed createBed(Bed bed) {
        bed.setBedId("BED-" + UUID.randomUUID().toString());
        bed.setIsOccupied(false);
        return bedRepository.save(bed);
    }

    // FR-16: Assign Bed to Patient
    public Bed assignBedToPatient(String bedId, String patientId) {
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new RuntimeException("Bed not found"));

        if (bed.getIsOccupied()) {
            throw new RuntimeException("Bed is already occupied");
        }

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Check if patient already has a bed assigned
        bedRepository.findByPatientId(patientId).ifPresent(existingBed -> {
            throw new RuntimeException("Patient already has a bed assigned");
        });

        bed.setPatient(patient);
        bed.setIsOccupied(true);

        return bedRepository.save(bed);
    }

    // FR-16: Release Bed from Patient (discharge)
    public Bed releaseBed(String bedId) {
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new RuntimeException("Bed not found"));

        if (!bed.getIsOccupied()) {
            throw new RuntimeException("Bed is not occupied");
        }

        bed.setPatient(null);
        bed.setIsOccupied(false);

        return bedRepository.save(bed);
    }

    // Release bed by patient ID
    public Bed releaseBedByPatient(String patientId) {
        Bed bed = bedRepository.findByPatientId(patientId)
            .orElseThrow(() -> new RuntimeException("No bed assigned to this patient"));

        bed.setPatient(null);
        bed.setIsOccupied(false);

        return bedRepository.save(bed);
    }

    // FR-17: Check Bed Availability
    public List<Bed> getAvailableBeds() {
        return bedRepository.findByIsOccupied(false);
    }

    public List<Bed> getAvailableBedsByDepartment(String departmentId) {
        return bedRepository.findAvailableBedsByDepartment(departmentId);
    }

    public Long countAvailableBedsByDepartment(String departmentId) {
        return bedRepository.countAvailableBedsByDepartment(departmentId);
    }

    public Bed getBedById(String bedId) {
        return bedRepository.findById(bedId)
            .orElseThrow(() -> new RuntimeException("Bed not found"));
    }

    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    public List<Bed> getBedsByDepartment(String departmentId) {
        return bedRepository.findByDepartmentId(departmentId);
    }

    public List<Bed> getOccupiedBeds() {
        return bedRepository.findByIsOccupied(true);
    }

    public Bed getBedByPatient(String patientId) {
        return bedRepository.findByPatientId(patientId)
            .orElseThrow(() -> new RuntimeException("No bed assigned to this patient"));
    }

    public Bed updateBed(String bedId, Bed updatedBed) {
        Bed existingBed = getBedById(bedId);

        if (updatedBed.getDepartment() != null) {
            existingBed.setDepartment(updatedBed.getDepartment());
        }
        if (updatedBed.getDailyRate() != null) {
            existingBed.setDailyRate(updatedBed.getDailyRate());
        }

        return bedRepository.save(existingBed);
    }

    public void deleteBed(String bedId) {
        Bed bed = getBedById(bedId);
        
        if (bed.getIsOccupied()) {
            throw new RuntimeException("Cannot delete occupied bed");
        }

        bedRepository.deleteById(bedId);
    }
}