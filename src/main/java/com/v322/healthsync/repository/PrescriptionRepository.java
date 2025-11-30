package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, String> {
    
    List<Prescription> findByPatient(Patient patient);
    
    List<Prescription> findByDoctor(Doctor doctor);
    
    List<Prescription> findByStatus(String status);
    
    List<Prescription> findByDateIssued(LocalDate dateIssued);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.personId = :patientId")
    List<Prescription> findByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor.personId = :doctorId")
    List<Prescription> findByDoctorId(@Param("doctorId") String doctorId);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.personId = :patientId AND p.status = :status")
    List<Prescription> findByPatientIdAndStatus(@Param("patientId") String patientId, 
                                                 @Param("status") String status);
    
    @Query("SELECT p FROM Prescription p WHERE p.dateIssued BETWEEN :startDate AND :endDate")
    List<Prescription> findByDateRange(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
}