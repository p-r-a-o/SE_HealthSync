package com.v322.healthsync.repository;
import com.v322.healthsync.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface PatientRepository extends JpaRepository<Patient,String> {
    Patient findByEmail(String email);
    
    Patient findByContactNumber(String contactNumber);
    
    List<Patient> findByCity(String city);
    
    List<Patient> findByRegistrationDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM Patient p WHERE p.firstName LIKE %:name% OR p.lastName LIKE %:name%")
    List<Patient> searchByName(@Param("name") String name);
    
    @Query("SELECT p FROM Patient p WHERE p.bloodGroup = :bloodGroup")
    List<Patient> findByBloodGroup(@Param("bloodGroup") String bloodGroup);

}