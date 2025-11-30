package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Bill;
import com.v322.healthsync.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    
    List<Bill> findByPatient(Patient patient);
    
    List<Bill> findByStatus(String status);
    
    List<Bill> findByBillDate(LocalDate billDate);
    
    @Query("SELECT b FROM Bill b WHERE b.patient.personId = :patientId")
    List<Bill> findByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT b FROM Bill b WHERE b.patient.personId = :patientId AND b.status = :status")
    List<Bill> findByPatientIdAndStatus(@Param("patientId") String patientId, 
                                         @Param("status") String status);
    
    @Query("SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate")
    List<Bill> findByDateRange(@Param("startDate") LocalDate startDate, 
                                @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.patient.personId = :patientId")
    BigDecimal calculateTotalAmountByPatient(@Param("patientId") String patientId);
    
    @Query("SELECT b FROM Bill b WHERE b.totalAmount > b.paidAmount")
    List<Bill> findUnpaidBills();
    
    @Query("SELECT b FROM Bill b WHERE b.patient.personId = :patientId AND b.totalAmount > b.paidAmount")
    List<Bill> findUnpaidBillsByPatient(@Param("patientId") String patientId);
}
