package com.v322.healthsync.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.v322.healthsync.entity.*;


public class DTOMapper {

    public static PatientDTO toPatientDTO(Patient patient) {
        if (patient == null) return null;
        
        PatientDTO dto = new PatientDTO();
        dto.setPersonId(patient.getPersonId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setContactNumber(patient.getContactNumber());
        dto.setEmail(patient.getEmail());
        dto.setCity(patient.getCity());
        dto.setRegistrationDate(patient.getRegistrationDate());
        dto.setNotes(patient.getNotes());
        return dto;
    }
    
    public static ReceptionistDTO toReceptionistDTO(Receptionist receptionist) {
        if (receptionist == null) return null;
        
        ReceptionistDTO dto = ReceptionistDTO.builder()
            .personId(receptionist.getPersonId())
            .firstName(receptionist.getFirstName())
            .lastName(receptionist.getLastName())
            .email(receptionist.getEmail())
            .contactNumber(receptionist.getContactNumber())
            .build();
        
        return dto;
    }
    public static PharmacistDTO toPharmacistDTO(Pharmacist pharmacist) {
        if (pharmacist == null) return null;
        
        PharmacistDTO dto = PharmacistDTO.builder()
            .personId(pharmacist.getPersonId())
            .firstName(pharmacist.getFirstName())
            .lastName(pharmacist.getLastName())
            .email(pharmacist.getEmail())
            .contactNumber(pharmacist.getContactNumber())
            .build();
        
        return dto;
    }

    public static DoctorDTO toDoctorDTO(Doctor doctor) {
        if (doctor == null) return null;
        
        DoctorDTO dto = new DoctorDTO();
        dto.setPersonId(doctor.getPersonId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setEmail(doctor.getEmail());
        dto.setContactNumber(doctor.getContactNumber());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setQualification(doctor.getQualification());
        dto.setConsultationFee(doctor.getConsultationFee());
        
        if (doctor.getDepartment() != null) {
            dto.setDepartmentId(doctor.getDepartment().getDepartmentId());
            dto.setDepartmentName(doctor.getDepartment().getName());
        }
        
        return dto;
    }

    public static AppointmentDTO toAppointmentDTO(Appointment appointment) {
        if (appointment == null) return null;
        
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(appointment.getAppointmentId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setDuration(appointment.getDuration());
        dto.setType(appointment.getType());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        dto.setDiagnosis(appointment.getDiagnosis());
        dto.setTreatmentPlan(appointment.getTreatmentPlan());
        
        if (appointment.getPatient() != null) {
            dto.setPatientId(appointment.getPatient().getPersonId());
            dto.setPatientName(appointment.getPatient().getFirstName() + " " + 
                              appointment.getPatient().getLastName());
        }
        
        if (appointment.getDoctor() != null) {
            dto.setDoctorId(appointment.getDoctor().getPersonId());
            dto.setDoctorName("Dr. " + appointment.getDoctor().getFirstName() + " " + 
                            appointment.getDoctor().getLastName());
        }
        
        return dto;
    }

    public static BedDTO toBedDTO(Bed bed) {
        if (bed == null) return null;
        
        BedDTO dto = BedDTO.builder()
            .bedId(bed.getBedId())
            .isOccupied(bed.getIsOccupied())
            .dailyRate(bed.getDailyRate())
            .build();
        
        if (bed.getDepartment() != null) {
            dto.setDepartmentId(bed.getDepartment().getDepartmentId());
            dto.setDepartmentName(bed.getDepartment().getName());
        }
        
        if (bed.getPatient() != null) {
            dto.setPatientId(bed.getPatient().getPersonId());
            dto.setPatientName(bed.getPatient().getFirstName() + " " + 
                             bed.getPatient().getLastName());
        }
        
        return dto;
    }
    public static BillItemDTO toBillItemDTO(BillItem billItem) {
        if (billItem == null) return null;
        
        BillItemDTO dto = BillItemDTO.builder()
            .itemId(billItem.getItemId())
            .description(billItem.getDescription())
            .totalPrice(billItem.getTotalPrice())
            .quantity(billItem.getQuantity())
            .build();
        
        if (billItem.getBill() != null) {
            dto.setBillId(billItem.getBill().getBillId());
        }
        
        if (billItem.getQuantity() != null && billItem.getTotalPrice() != null 
            && billItem.getQuantity() > 0) {
            dto.setUnitPrice(billItem.getTotalPrice()
                .divide(new BigDecimal(billItem.getQuantity()), 2, BigDecimal.ROUND_HALF_UP));
        }
        
        return dto;
    }
    public static BillDTO toBillDTO(Bill bill) {
        if (bill == null) return null;
        
        BillDTO dto = BillDTO.builder()
            .billId(bill.getBillId())
            .billDate(bill.getBillDate())
            .totalAmount(bill.getTotalAmount())
            .paidAmount(bill.getPaidAmount())
            .status(bill.getStatus())
            .build();
        
        if (bill.getTotalAmount() != null && bill.getPaidAmount() != null) {
            dto.setBalanceAmount(bill.getTotalAmount().subtract(bill.getPaidAmount()));
        }
        
        if (bill.getPatient() != null) {
            dto.setPatientId(bill.getPatient().getPersonId());
            dto.setPatientName(bill.getPatient().getFirstName() + " " + 
                             bill.getPatient().getLastName());
        }
        
        if (bill.getBillItems() != null) {
            dto.setBillItems(bill.getBillItems().stream()
                .map(DTOMapper::toBillItemDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public static DepartmentDTO toDepartmentDTO(Department department) {
        if (department == null) return null;
        
        DepartmentDTO dto = DepartmentDTO.builder()
            .departmentId(department.getDepartmentId())
            .name(department.getName())
            .location(department.getLocation())
            .build();
        
        if (department.getDoctors() != null) {
            dto.setTotalDoctors(department.getDoctors().size());
        }
        
        if (department.getBeds() != null) {
            dto.setTotalBeds(department.getBeds().size());
            long availableBeds = department.getBeds().stream()
                .filter(bed -> !bed.getIsOccupied())
                .count();
            dto.setAvailableBeds((int) availableBeds);
        }
        
        return dto;
    }

    public static DoctorAvailabilityDTO toDoctorAvailabilityDTO(DoctorAvailability availability) {
        if (availability == null) return null;
        
        DoctorAvailabilityDTO dto = DoctorAvailabilityDTO.builder()
            .slotId(availability.getSlotId())
            .startTime(availability.getStartTime())
            .endTime(availability.getEndTime())
            .dayOfWeek(availability.getDayOfWeek())
            .build();
        
        if (availability.getDoctor() != null) {
            dto.setDoctorId(availability.getDoctor().getPersonId());
            dto.setDoctorName(availability.getDoctor().getFirstName() + " " + 
                            availability.getDoctor().getLastName());
        }
        
        return dto;
    }
    public static PharmacyDTO toPharmacyDTO(Pharmacy pharmacy) {
        if (pharmacy == null) return null;
        
        PharmacyDTO dto = PharmacyDTO.builder()
            .pharmacyId(pharmacy.getPharmacyId())
            .location(pharmacy.getLocation())
            .build();
        
        if (pharmacy.getPharmacist() != null) {
            dto.setPharmacistId(pharmacy.getPharmacist().getPersonId());
            dto.setPharmacistName(pharmacy.getPharmacist().getFirstName() + " " + 
                                pharmacy.getPharmacist().getLastName());
        }
        
        if (pharmacy.getMedications() != null) {
            dto.setTotalMedications(pharmacy.getMedications().size());
        }
        
        return dto;
    }
    public static MedicationDTO toMedicationDTO(Medication medication) {
        if (medication == null) return null;
        
        MedicationDTO dto = MedicationDTO.builder()
            .medicationId(medication.getMedicationId())
            .name(medication.getName())
            .genericName(medication.getGenericName())
            .manufacturer(medication.getManufacturer())
            .description(medication.getDescription())
            .unitPrice(medication.getUnitPrice())
            .quantity(medication.getQuantity())
            .build();
        
        if (medication.getPharmacy() != null) {
            dto.setPharmacyId(medication.getPharmacy().getPharmacyId());
            dto.setPharmacyLocation(medication.getPharmacy().getLocation());
        }
        
        return dto;
    }
    public static PrescriptionItemDTO toPrescriptionItemDTO(PrescriptionItem item) {
        if (item == null) return null;
        
        PrescriptionItemDTO dto = PrescriptionItemDTO.builder()
            .prescriptionItemId(item.getPrescriptionItemId())
            .quantity(item.getQuantity())
            .build();
        
        if (item.getPrescription() != null) {
            dto.setPrescriptionId(item.getPrescription().getPrescriptionId());
        }
        
        if (item.getMedication() != null) {
            dto.setMedicationId(item.getMedication().getMedicationId());
            dto.setMedicationName(item.getMedication().getName());
            dto.setGenericName(item.getMedication().getGenericName());
            dto.setUnitPrice(item.getMedication().getUnitPrice());
            
            if (item.getQuantity() != null && item.getMedication().getUnitPrice() != null) {
                dto.setTotalPrice(item.getMedication().getUnitPrice()
                    .multiply(new BigDecimal(item.getQuantity())));
            }
        }
        
        return dto;
    }
    public static PrescriptionDTO toPrescriptionDTO(Prescription prescription) {
        if (prescription == null) return null;
        
        PrescriptionDTO dto = PrescriptionDTO.builder()
            .prescriptionId(prescription.getPrescriptionId())
            .dateIssued(prescription.getDateIssued())
            .status(prescription.getStatus())
            .instructions(prescription.getInstructions())
            .build();
        
        if (prescription.getPatient() != null) {
            dto.setPatientId(prescription.getPatient().getPersonId());
            dto.setPatientName(prescription.getPatient().getFirstName() + " " + 
                             prescription.getPatient().getLastName());
        }
        
        if (prescription.getDoctor() != null) {
            dto.setDoctorId(prescription.getDoctor().getPersonId());
            dto.setDoctorName(prescription.getDoctor().getFirstName() + " " + 
                            prescription.getDoctor().getLastName());
        }
        
        if (prescription.getPrescriptionItems() != null) {
            dto.setPrescriptionItems(prescription.getPrescriptionItems().stream()
                .map(DTOMapper::toPrescriptionItemDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
}