package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Medication;
import com.v322.healthsync.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, String> {
    
    Optional<Medication> findByName(String name);
    
    List<Medication> findByGenericName(String genericName);
    
    List<Medication> findByManufacturer(String manufacturer);
    
    List<Medication> findByPharmacy(Pharmacy pharmacy);
    
    @Query("SELECT m FROM Medication m WHERE m.pharmacy.pharmacyId = :pharmacyId")
    List<Medication> findByPharmacyId(@Param("pharmacyId") String pharmacyId);
    
    @Query("SELECT m FROM Medication m WHERE m.name LIKE %:keyword% OR m.genericName LIKE %:keyword%")
    List<Medication> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT m FROM Medication m WHERE m.unitPrice BETWEEN :minPrice AND :maxPrice")
    List<Medication> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                       @Param("maxPrice") BigDecimal maxPrice);
}