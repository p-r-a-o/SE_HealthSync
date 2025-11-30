package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Appointment;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    
    List<Appointment> findByPatient(Patient patient);
    
    List<Appointment> findByDoctor(Doctor doctor);
    
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
    
    List<Appointment> findByStatus(String status);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.personId = :patientId")
    List<Appointment> findByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.personId = :doctorId")
    List<Appointment> findByDoctorId(@Param("doctorId") String doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.personId = :doctorId AND a.appointmentDate = :date")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") String doctorId, 
                                             @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.personId = :patientId AND a.appointmentDate = :date")
    List<Appointment> findByPatientIdAndDate(@Param("patientId") String patientId, 
                                              @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate")
    List<Appointment> findByDateRange(@Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.personId = :doctorId AND a.appointmentDate = :date " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findConflictingAppointments(@Param("doctorId") String doctorId, 
                                                   @Param("date") LocalDate date,
                                                   @Param("startTime") LocalTime startTime, 
                                                   @Param("endTime") LocalTime endTime);
}