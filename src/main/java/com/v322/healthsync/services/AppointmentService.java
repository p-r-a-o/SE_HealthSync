package com.v322.healthsync.service;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    // FR-3: Book Appointment
    public Appointment bookAppointment(Appointment appointment) {
        // Validate doctor availability
        if (!isDoctorAvailable(appointment.getDoctor().getPersonId(), 
                               appointment.getAppointmentDate(),
                               appointment.getStartTime(), 
                               appointment.getEndTime())) {
            throw new RuntimeException("Doctor is not available at the requested time");
        }

        // Check for conflicting appointments
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
            appointment.getDoctor().getPersonId(),
            appointment.getAppointmentDate(),
            appointment.getStartTime(),
            appointment.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot conflicts with existing appointment");
        }

        appointment.setAppointmentId("APT-" + UUID.randomUUID().toString());
        appointment.setStatus("SCHEDULED");
        return appointmentRepository.save(appointment);
    }

    // FR-4: Check if doctor is available
    private boolean isDoctorAvailable(String doctorId, LocalDate date, 
                                     LocalTime startTime, LocalTime endTime) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
            .findByDoctorIdAndDay(doctorId, dayOfWeek.toString());

        for (DoctorAvailability availability : availabilities) {
            if (!startTime.isBefore(availability.getStartTime()) && 
                !endTime.isAfter(availability.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    // Get available time slots for a doctor on a specific date
    public List<TimeSlot> getAvailableSlots(String doctorId, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<DoctorAvailability> availabilities = doctorAvailabilityRepository
            .findByDoctorIdAndDay(doctorId, dayOfWeek.toString());

        List<Appointment> bookedAppointments = appointmentRepository
            .findByDoctorIdAndDate(doctorId, date);

        List<TimeSlot> availableSlots = new ArrayList<>();

        for (DoctorAvailability availability : availabilities) {
            LocalTime currentTime = availability.getStartTime();
            LocalTime endTime = availability.getEndTime();

            while (currentTime.plusMinutes(30).isBefore(endTime) || 
                   currentTime.plusMinutes(30).equals(endTime)) {
                LocalTime slotEnd = currentTime.plusMinutes(30);
                
                boolean isAvailable = true;
                for (Appointment appointment : bookedAppointments) {
                    if (!(slotEnd.isBefore(appointment.getStartTime()) || 
                          currentTime.isAfter(appointment.getEndTime()))) {
                        isAvailable = false;
                        break;
                    }
                }

                if (isAvailable) {
                    availableSlots.add(new TimeSlot(currentTime, slotEnd));
                }

                currentTime = slotEnd;
            }
        }

        return availableSlots;
    }

    // FR-5: Update Appointment
    public Appointment updateAppointment(String appointmentId, Appointment updatedAppointment) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (updatedAppointment.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
        }
        if (updatedAppointment.getStartTime() != null) {
            existingAppointment.setStartTime(updatedAppointment.getStartTime());
        }
        if (updatedAppointment.getEndTime() != null) {
            existingAppointment.setEndTime(updatedAppointment.getEndTime());
        }
        if (updatedAppointment.getDoctor() != null) {
            existingAppointment.setDoctor(updatedAppointment.getDoctor());
        }
        if (updatedAppointment.getType() != null) {
            existingAppointment.setType(updatedAppointment.getType());
        }
        if (updatedAppointment.getStatus() != null) {
            existingAppointment.setStatus(updatedAppointment.getStatus());
        }

        return appointmentRepository.save(existingAppointment);
    }

    // FR-6: Cancel Appointment
    public void cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus("CANCELLED");
        appointmentRepository.save(appointment);
    }

    // FR-7: View Appointment Details
    public Appointment getAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    // FR-8: Conduct Consultation (update consultation details)
    public Appointment conductConsultation(String appointmentId, String diagnosis, 
                                          String treatmentPlan, String notes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setDiagnosis(diagnosis);
        appointment.setTreatmentPlan(treatmentPlan);
        appointment.setNotes(notes);
        appointment.setStatus("COMPLETED");

        return appointmentRepository.save(appointment);
    }

    // Inner class for time slot response
    public static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;

        public TimeSlot(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
    }
}