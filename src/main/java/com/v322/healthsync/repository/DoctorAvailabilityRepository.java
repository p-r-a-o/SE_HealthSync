package com.v322.healthsync.repository;

import com.v322.healthsync.entity.DoctorAvailability;
import com.v322.healthsync.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, String> {
    
    List<DoctorAvailability> findByDoctor(Doctor doctor);
    
    List<DoctorAvailability> findByDayOfWeek(String dayOfWeek);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor.personId = :doctorId")
    List<DoctorAvailability> findByDoctorId(@Param("doctorId") String doctorId);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor.personId = :doctorId AND da.dayOfWeek = :day")
    List<DoctorAvailability> findByDoctorIdAndDay(@Param("doctorId") String doctorId, 
                                                   @Param("day") String dayOfWeek);
}
