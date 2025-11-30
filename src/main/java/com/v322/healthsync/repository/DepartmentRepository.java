package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    
    Optional<Department> findByName(String name);
    
    List<Department> findByLocation(String location);
    
    @Query("SELECT d FROM Department d WHERE d.name LIKE %:keyword%")
    List<Department> searchByName(@Param("keyword") String keyword);
}