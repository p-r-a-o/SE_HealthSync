package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Pharmacist;
import com.v322.healthsync.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, String> {
    
    Pharmacist findByEmail(String email);
    
    Pharmacist findByPharmacy(Pharmacy pharmacy);
    
    @Query("SELECT p FROM Pharmacist p WHERE p.pharmacy.pharmacyId = :pharmacyId")
    Pharmacist findByPharmacyId(@Param("pharmacyId") String pharmacyId);
}