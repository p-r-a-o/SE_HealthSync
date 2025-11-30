package com.v322.healthsync.repository;

import com.v322.healthsync.entity.BillItem;
import com.v322.healthsync.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, String> {
    
    List<BillItem> findByBill(Bill bill);
    
    @Query("SELECT bi FROM BillItem bi WHERE bi.bill.billId = :billId")
    List<BillItem> findByBillId(@Param("billId") String billId);
    
    @Query("SELECT bi FROM BillItem bi JOIN bi.bill b WHERE b.patient.personId = :patientId")
    List<BillItem> findByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT bi FROM BillItem bi WHERE bi.description LIKE %:keyword%")
    List<BillItem> searchByDescription(@Param("keyword") String keyword);
}