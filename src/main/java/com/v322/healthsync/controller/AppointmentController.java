package com.v322.healthsync.controller;

import com.v322.healthsync.dto.*;
import com.v322.healthsync.entity.Appointment;
import com.v322.healthsync.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private EntityMapper entityMapper;

    // FR-3: Book Appointment
    @PostMapping
    public ResponseEntity<AppointmentDTO> bookAppointment(@RequestBody AppointmentDTO appointment) {
        try {
            Appointment bookedAppointment = appointmentService.bookAppointment(entityMapper.toAppointmentEntity(appointment));
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(bookedAppointment), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-4: View Doctor Availability - Get Available Slots
    @GetMapping("/available-slots")
    public ResponseEntity<List<AppointmentService.TimeSlot>> getAvailableSlots(
            @RequestParam String doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<AppointmentService.TimeSlot> slots = appointmentService.getAvailableSlots(doctorId, date);
            return new ResponseEntity<>(slots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-5: Update Appointment
    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable String appointmentId,
                                                         @RequestBody AppointmentDTO appointment) {
        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, entityMapper.toAppointmentEntity(appointment));
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(updatedAppointment), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-6: Cancel Appointment
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable String appointmentId) {
        try {
            appointmentService.cancelAppointment(appointmentId);
            return new ResponseEntity<>("Appointment cancelled successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to cancel appointment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-7: View Appointment Details
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(appointment), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatient(@PathVariable String patientId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
            List<AppointmentDTO> responseList = appointments.stream().map(DTOMapper::toAppointmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctor(@PathVariable String doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
            List<AppointmentDTO> responseList = appointments.stream().map(DTOMapper::toAppointmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
            List<AppointmentDTO> responseList = appointments.stream().map(DTOMapper::toAppointmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByStatus(@PathVariable String status) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
            List<AppointmentDTO> responseList = appointments.stream().map(DTOMapper::toAppointmentDTO).toList();
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // FR-8: Conduct Consultation
    @PutMapping("/{appointmentId}/consultation")
    public ResponseEntity<AppointmentDTO> conductConsultation(
            @PathVariable String appointmentId,
            @RequestParam String diagnosis,
            @RequestParam String treatmentPlan,
            @RequestParam(required = false) String notes) {
        try {
            Appointment appointment = appointmentService.conductConsultation(
                appointmentId, diagnosis, treatmentPlan, notes);
            return new ResponseEntity<>(DTOMapper.toAppointmentDTO(appointment), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}