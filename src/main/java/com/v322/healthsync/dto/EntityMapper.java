package com.v322.healthsync.dto;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.v322.healthsync.entity.*;
import com.v322.healthsync.repository.*;

@Component
public class EntityMapper {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final PharmacyRepository pharmacyRepository;
    private final MedicationRepository medicationRepository;
    private final BillRepository billRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public EntityMapper(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            DepartmentRepository departmentRepository,
            PharmacyRepository pharmacyRepository,
            MedicationRepository medicationRepository,
            BillRepository billRepository,
            PrescriptionRepository prescriptionRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.medicationRepository = medicationRepository;
        this.billRepository = billRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public Patient toPatientEntity(PatientDTO dto) {
        if (dto == null) return null;
        
        Patient patient = new Patient();
        patient.setPersonId(dto.getPersonId());
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setContactNumber(dto.getContactNumber());
        patient.setEmail(dto.getEmail());
        patient.setCity(dto.getCity());
        patient.setRegistrationDate(dto.getRegistrationDate());
        patient.setNotes(dto.getNotes());
        return patient;
    }

    public Patient toPatientEntity(PatientRegisterDTO dto) {
        if (dto == null) return null;
        
        Patient patient = new Patient();
        patient.setPersonId(dto.getPersonId());
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setContactNumber(dto.getContactNumber());
        patient.setEmail(dto.getEmail());
        patient.setCity(dto.getCity());
        patient.setRegistrationDate(LocalDate.now());
        patient.setNotes(dto.getNotes());
        patient.setPassword(dto.getPassword());
        return patient;
    }
    
    public Receptionist toReceptionistEntity(ReceptionistDTO dto) {
        if (dto == null) return null;
        
        Receptionist receptionist = new Receptionist();
        receptionist.setPersonId(dto.getPersonId());
        receptionist.setFirstName(dto.getFirstName());
        receptionist.setLastName(dto.getLastName());
        receptionist.setEmail(dto.getEmail());
        receptionist.setContactNumber(dto.getContactNumber());
        return receptionist;
    }
    
    public Pharmacist toPharmacistEntity(PharmacistDTO dto) {
        if (dto == null) return null;
        
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setPersonId(dto.getPersonId());
        pharmacist.setFirstName(dto.getFirstName());
        pharmacist.setLastName(dto.getLastName());
        pharmacist.setEmail(dto.getEmail());
        pharmacist.setContactNumber(dto.getContactNumber());
        return pharmacist;
    }

    public Doctor toDoctorEntity(DoctorDTO dto) {
        if (dto == null) return null;
        
        Doctor doctor = new Doctor();
        doctor.setPersonId(dto.getPersonId());
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setEmail(dto.getEmail());
        doctor.setContactNumber(dto.getContactNumber());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setQualification(dto.getQualification());
        doctor.setConsultationFee(dto.getConsultationFee());
        
        // Set department relationship if departmentId is provided
        if (dto.getDepartmentId() != null) {
            departmentRepository.findById(dto.getDepartmentId())
                .ifPresent(doctor::setDepartment);
        }
        
        return doctor;
    }

    public Appointment toAppointmentEntity(AppointmentDTO dto) {
        if (dto == null) return null;
        
        Appointment appointment = Appointment.builder()
            .appointmentId(dto.getAppointmentId())
            .appointmentDate(dto.getAppointmentDate())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .duration(dto.getDuration())
            .type(dto.getType())
            .status(dto.getStatus())
            .notes(dto.getNotes())
            .diagnosis(dto.getDiagnosis())
            .treatmentPlan(dto.getTreatmentPlan())
            .build();
        
        // Set patient relationship if patientId is provided
        if (dto.getPatientId() != null) {
            patientRepository.findById(dto.getPatientId())
                .ifPresent(appointment::setPatient);
        }
        
        // Set doctor relationship if doctorId is provided
        if (dto.getDoctorId() != null) {
            doctorRepository.findById(dto.getDoctorId())
                .ifPresent(appointment::setDoctor);
        }
        
        return appointment;
    }

    public Bed toBedEntity(BedDTO dto) {
        if (dto == null) return null;
        
        Bed bed = Bed.builder()
            .bedId(dto.getBedId())
            .isOccupied(dto.getIsOccupied())
            .dailyRate(dto.getDailyRate())
            .build();
        
        // Set department relationship if departmentId is provided
        if (dto.getDepartmentId() != null) {
            departmentRepository.findById(dto.getDepartmentId())
                .ifPresent(bed::setDepartment);
        }
        
        // Set patient relationship if patientId is provided
        if (dto.getPatientId() != null) {
            patientRepository.findById(dto.getPatientId())
                .ifPresent(bed::setPatient);
        }
        
        return bed;
    }
    
    public BillItem toBillItemEntity(BillItemDTO dto) {
        if (dto == null) return null;
        
        BillItem billItem = BillItem.builder()
            .itemId(dto.getItemId())
            .description(dto.getDescription())
            .totalPrice(dto.getTotalPrice())
            .quantity(dto.getQuantity())
            .build();
        
        // Set bill relationship if billId is provided
        if (dto.getBillId() != null) {
            billRepository.findById(dto.getBillId())
                .ifPresent(billItem::setBill);
        }
        
        return billItem;
    }
    
    public Bill toBillEntity(BillDTO dto) {
        if (dto == null) return null;
        
        Bill bill = Bill.builder()
            .billId(dto.getBillId())
            .billDate(dto.getBillDate())
            .totalAmount(dto.getTotalAmount())
            .paidAmount(dto.getPaidAmount())
            .status(dto.getStatus())
            .build();
        
        // Set patient relationship if patientId is provided
        if (dto.getPatientId() != null) {
            patientRepository.findById(dto.getPatientId())
                .ifPresent(bill::setPatient);
        }
        
        // Convert bill items if present
        if (dto.getBillItems() != null && !dto.getBillItems().isEmpty()) {
            bill.setBillItems(dto.getBillItems().stream()
                .map(this::toBillItemEntity)
                .collect(Collectors.toList()));
            
            // Set parent bill reference for each item
            bill.getBillItems().forEach(item -> item.setBill(bill));
        }
        
        return bill;
    }
    
    public Department toDepartmentEntity(DepartmentDTO dto) {
        if (dto == null) return null;
        
        Department department = Department.builder()
            .departmentId(dto.getDepartmentId())
            .name(dto.getName())
            .location(dto.getLocation())
            .build();
        
        return department;
    }

    public DoctorAvailability toDoctorAvailabilityEntity(DoctorAvailabilityDTO dto) {
        if (dto == null) return null;
        
        DoctorAvailability availability = DoctorAvailability.builder()
            .slotId(dto.getSlotId())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .dayOfWeek(dto.getDayOfWeek())
            .build();
        
        // Set doctor relationship if doctorId is provided
        if (dto.getDoctorId() != null) {
            doctorRepository.findById(dto.getDoctorId())
                .ifPresent(availability::setDoctor);
        }
        
        return availability;
    }
    
    public Pharmacy toPharmacyEntity(PharmacyDTO dto) {
        if (dto == null) return null;
        
        Pharmacy pharmacy = Pharmacy.builder()
            .pharmacyId(dto.getPharmacyId())
            .location(dto.getLocation())
            .build();
        
        return pharmacy;
    }
    
    public Medication toMedicationEntity(MedicationDTO dto) {
        if (dto == null) return null;
        
        Medication medication = Medication.builder()
            .medicationId(dto.getMedicationId())
            .name(dto.getName())
            .genericName(dto.getGenericName())
            .manufacturer(dto.getManufacturer())
            .description(dto.getDescription())
            .unitPrice(dto.getUnitPrice())
            .build();
        
        // Set pharmacy relationship if pharmacyId is provided
        if (dto.getPharmacyId() != null) {
            pharmacyRepository.findById(dto.getPharmacyId())
                .ifPresent(medication::setPharmacy);
        }
        
        return medication;
    }
    
    public PrescriptionItem toPrescriptionItemEntity(PrescriptionItemDTO dto) {
        if (dto == null) return null;
        
        PrescriptionItem item = PrescriptionItem.builder()
            .prescriptionItemId(dto.getPrescriptionItemId())
            .quantity(dto.getQuantity())
            .build();
        
        // Set prescription relationship if prescriptionId is provided
        if (dto.getPrescriptionId() != null) {
            prescriptionRepository.findById(dto.getPrescriptionId())
                .ifPresent(item::setPrescription);
        }
        
        // Set medication relationship if medicationId is provided
        if (dto.getMedicationId() != null) {
            medicationRepository.findById(dto.getMedicationId())
                .ifPresent(item::setMedication);
        }
        
        return item;
    }
    
    public Prescription toPrescriptionEntity(PrescriptionDTO dto) {
        if (dto == null) return null;
        
        Prescription prescription = Prescription.builder()
            .prescriptionId(dto.getPrescriptionId())
            .dateIssued(dto.getDateIssued())
            .status(dto.getStatus())
            .instructions(dto.getInstructions())
            .build();
        
        // Set patient relationship if patientId is provided
        if (dto.getPatientId() != null) {
            patientRepository.findById(dto.getPatientId())
                .ifPresent(prescription::setPatient);
        }
        
        // Set doctor relationship if doctorId is provided
        if (dto.getDoctorId() != null) {
            doctorRepository.findById(dto.getDoctorId())
                .ifPresent(prescription::setDoctor);
        }
        
        // Convert prescription items if present
        if (dto.getPrescriptionItems() != null && !dto.getPrescriptionItems().isEmpty()) {
            prescription.setPrescriptionItems(dto.getPrescriptionItems().stream()
                .map(this::toPrescriptionItemEntity)
                .collect(Collectors.toList()));
            
            // Set parent prescription reference for each item
            prescription.getPrescriptionItems().forEach(item -> item.setPrescription(prescription));
        }
        
        return prescription;
    }
}