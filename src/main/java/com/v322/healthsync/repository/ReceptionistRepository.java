package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, String> {
    
    Receptionist findByEmail(String email);
    
    Receptionist findByContactNumber(String contactNumber);
}