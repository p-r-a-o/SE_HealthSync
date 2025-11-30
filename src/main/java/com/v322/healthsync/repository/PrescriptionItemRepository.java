package com.v322.healthsync.repository;

import com.v322.healthsync.entity.PrescriptionItem;
import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, String> {
    
    List<PrescriptionItem> findByPrescription(Prescription prescription);
    
    List<PrescriptionItem> findByMedication(Medication medication);
    
    @Query("SELECT pi FROM PrescriptionItem pi WHERE pi.prescription.prescriptionId = :prescriptionId")
    List<PrescriptionItem> findByPrescriptionId(@Param("prescriptionId") String prescriptionId);
    
    @Query("SELECT pi FROM PrescriptionItem pi WHERE pi.medication.medicationId = :medicationId")
    List<PrescriptionItem> findByMedicationId(@Param("medicationId") String medicationId);
    
    @Query("SELECT pi FROM PrescriptionItem pi JOIN pi.prescription p WHERE p.patient.personId = :patientId")
    List<PrescriptionItem> findByPatientId(@Param("patientId") String patientId);
}