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
}package com.v322.healthsync.repository;

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
}package com.v322.healthsync.repository;

import com.v322.healthsync.entity.BillItem;
import com.v322.healthsync.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, String> {
    
    List<BillItem> findByBill(Bill bill);
    
    @Query("SELECT bi FROM BillItem bi WHERE bi.bill.billId = :billId")
    List<BillItem> findByBillId(@Param("billId") String billId);
    
    @Query("SELECT bi FROM BillItem bi JOIN bi.bill b WHERE b.patient.personId = :patientId")
    List<BillItem> findByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT bi FROM BillItem bi WHERE bi.description LIKE %:keyword%")
    List<BillItem> searchByDescription(@Param("keyword") String keyword);
}package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Bill;
import com.v322.healthsync.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    
    List<Bill> findByPatient(Patient patient);
    
    List<Bill> findByStatus(String status);
    
    List<Bill> findByBillDate(LocalDate billDate);
    
    @Query("SELECT b FROM Bill b WHERE b.patient.personId = :patientId")
    List<Bill> findByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT b FROM Bill b WHERE b.patient.personId = :patientId AND b.status = :status")
    List<Bill> findByPatientIdAndStatus(@Param("patientId") String patientId, 
                                         @Param("status") String status);
    
    @Query("SELECT b FROM Bill b WHERE b.billDate BETWEEN :startDate AND :endDate")
    List<Bill> findByDateRange(@Param("startDate") LocalDate startDate, 
                                @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.patient.personId = :patientId")
    BigDecimal calculateTotalAmountByPatient(@Param("patientId") String patientId);
    
    @Query("SELECT b FROM Bill b WHERE b.totalAmount > b.paidAmount")
    List<Bill> findUnpaidBills();
    
    @Query("SELECT b FROM Bill b WHERE b.patient.personId = :patientId AND b.totalAmount > b.paidAmount")
    List<Bill> findUnpaidBillsByPatient(@Param("patientId") String patientId);
}
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
}package com.v322.healthsync.repository;

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
}package com.v322.healthsync.repository;

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
}package com.v322.healthsync.repository;
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

}package com.v322.healthsync.repository;

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
}package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, String> {
    
    List<Pharmacy> findByLocation(String location);
}package com.v322.healthsync.repository;

import com.v322.healthsync.entity.PrescriptionItem;
import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, String> {
    
    List<PrescriptionItem> findByPrescription(Prescription prescription);
    
    List<PrescriptionItem> findByMedication(Medication medication);
    
    @Query("SELECT pi FROM PrescriptionItem pi WHERE pi.prescription.prescriptionId = :prescriptionId")
    List<PrescriptionItem> findByPrescriptionId(@Param("prescriptionId") String prescriptionId);
    
    @Query("SELECT pi FROM PrescriptionItem pi WHERE pi.medication.medicationId = :medicationId")
    List<PrescriptionItem> findByMedicationId(@Param("medicationId") String medicationId);
    
    @Query("SELECT pi FROM PrescriptionItem pi JOIN pi.prescription p WHERE p.patient.personId = :patientId")
    List<PrescriptionItem> findByPatientId(@Param("patientId") String patientId);
}package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Prescription;
import com.v322.healthsync.entity.Patient;
import com.v322.healthsync.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, String> {
    
    List<Prescription> findByPatient(Patient patient);
    
    List<Prescription> findByDoctor(Doctor doctor);
    
    List<Prescription> findByStatus(String status);
    
    List<Prescription> findByDateIssued(LocalDate dateIssued);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.personId = :patientId")
    List<Prescription> findByPatientId(@Param("patientId") String patientId);
    
    @Query("SELECT p FROM Prescription p WHERE p.doctor.personId = :doctorId")
    List<Prescription> findByDoctorId(@Param("doctorId") String doctorId);
    
    @Query("SELECT p FROM Prescription p WHERE p.patient.personId = :patientId AND p.status = :status")
    List<Prescription> findByPatientIdAndStatus(@Param("patientId") String patientId, 
                                                 @Param("status") String status);
    
    @Query("SELECT p FROM Prescription p WHERE p.dateIssued BETWEEN :startDate AND :endDate")
    List<Prescription> findByDateRange(@Param("startDate") LocalDate startDate, 
                                        @Param("endDate") LocalDate endDate);
}package com.v322.healthsync.repository;

import com.v322.healthsync.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, String> {
    
    Receptionist findByEmail(String email);
    
    Receptionist findByContactNumber(String contactNumber);
}package com.v322.healthsync.repository;

import com.v322.healthsync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

}