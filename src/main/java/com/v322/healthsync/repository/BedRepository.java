package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Bed;
import com.v322.healthsync.entity.Department;
import com.v322.healthsync.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, String> {
    
    List<Bed> findByDepartment(Department department);
    
    List<Bed> findByIsOccupied(Boolean isOccupied);
    
    Optional<Bed> findByPatient(Patient patient);
    
    @Query("SELECT b FROM Bed b WHERE b.department.departmentId = :deptId")
    List<Bed> findByDepartmentId(@Param("deptId") String departmentId);
    
    @Query("SELECT b FROM Bed b WHERE b.department.departmentId = :deptId AND b.isOccupied = false")
    List<Bed> findAvailableBedsByDepartment(@Param("deptId") String departmentId);
    
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.department.departmentId = :deptId AND b.isOccupied = false")
    Long countAvailableBedsByDepartment(@Param("deptId") String departmentId);
    
    @Query("SELECT b FROM Bed b WHERE b.patient.personId = :patientId")
    Optional<Bed> findByPatientId(@Param("patientId") String patientId);
}