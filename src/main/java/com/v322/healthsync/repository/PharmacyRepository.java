package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, String> {
    
    List<Pharmacy> findByLocation(String location);
}