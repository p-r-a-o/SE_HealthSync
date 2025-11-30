package com.v322.healthsync.repository;
import com.v322.healthsync.entity.Doctor;
import com.v322.healthsync.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor,String> {
    Doctor findByEmail(String email);
    
    List<Doctor> findByDepartment(Department department);
    
    List<Doctor> findBySpecialization(String specialization);
    
    @Query("SELECT d FROM Doctor d WHERE d.department.departmentId = :deptId")
    List<Doctor> findByDepartmentId(@Param("deptId") String departmentId);
    
    @Query("SELECT d FROM Doctor d WHERE d.consultationFee <= :maxFee")
    List<Doctor> findByConsultationFeeLessThanEqual(@Param("maxFee") BigDecimal maxFee);
    
    @Query("SELECT d FROM Doctor d WHERE d.firstName LIKE %:name% OR d.lastName LIKE %:name%")
    List<Doctor> searchByName(@Param("name") String name);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization = :specialization AND d.department.departmentId = :deptId")
    List<Doctor> findBySpecializationAndDepartment(@Param("specialization") String specialization, 
                                                     @Param("deptId") String departmentId);
}