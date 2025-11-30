package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Doctor getDoctorById(String doctorId) {
        return doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsByDepartment(String departmentId) {
        return doctorRepository.findByDepartmentId(departmentId);
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.searchByName(name);
    }

    public List<Doctor> getDoctorsByConsultationFee(BigDecimal maxFee) {
        return doctorRepository.findByConsultationFeeLessThanEqual(maxFee);
    }

    // FR-9: Manage Doctor Availability
    public DoctorAvailability addDoctorAvailability(DoctorAvailability availability) {
        availability.setSlotId("SLOT-" + UUID.randomUUID().toString());
        return doctorAvailabilityRepository.save(availability);
    }

    public DoctorAvailability updateDoctorAvailability(String slotId, DoctorAvailability updatedAvailability) {
        DoctorAvailability existingAvailability = doctorAvailabilityRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        existingAvailability.setStartTime(updatedAvailability.getStartTime());
        existingAvailability.setEndTime(updatedAvailability.getEndTime());
        existingAvailability.setDayOfWeek(updatedAvailability.getDayOfWeek());

        return doctorAvailabilityRepository.save(existingAvailability);
    }

    public void deleteDoctorAvailability(String slotId) {
        doctorAvailabilityRepository.deleteById(slotId);
    }

    public List<DoctorAvailability> getDoctorAvailability(String doctorId) {
        return doctorAvailabilityRepository.findByDoctorId(doctorId);
    }

    public List<DoctorAvailability> getDoctorAvailabilityByDay(String doctorId, String dayOfWeek) {
        return doctorAvailabilityRepository.findByDoctorIdAndDay(doctorId, dayOfWeek);
    }

    // FR-10: View Patient List
    public List<Patient> getPatientListForDoctor(String doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId);
        return appointments.stream()
            .map(Appointment::getPatient)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
    }

    // FR-11: Update Consultation Fee
    public Doctor updateConsultationFee(String doctorId, BigDecimal newFee) {
        Doctor doctor = getDoctorById(doctorId);
        doctor.setConsultationFee(newFee);
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(String doctorId, Doctor updatedDoctor) {
        Doctor existingDoctor = getDoctorById(doctorId);

        if (updatedDoctor.getFirstName() != null) {
            existingDoctor.setFirstName(updatedDoctor.getFirstName());
        }
        if (updatedDoctor.getLastName() != null) {
            existingDoctor.setLastName(updatedDoctor.getLastName());
        }
        if (updatedDoctor.getContactNumber() != null) {
            existingDoctor.setContactNumber(updatedDoctor.getContactNumber());
        }
        if (updatedDoctor.getEmail() != null) {
            existingDoctor.setEmail(updatedDoctor.getEmail());
        }
        if (updatedDoctor.getSpecialization() != null) {
            existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        }
        if (updatedDoctor.getQualification() != null) {
            existingDoctor.setQualification(updatedDoctor.getQualification());
        }
        if (updatedDoctor.getDepartment() != null) {
            existingDoctor.setDepartment(updatedDoctor.getDepartment());
        }

        return doctorRepository.save(existingDoctor);
    }

    public void deleteDoctor(String doctorId) {
        doctorRepository.deleteById(doctorId);
    }
}