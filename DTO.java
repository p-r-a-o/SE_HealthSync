package com.v322.healthsync.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;
import com.v322.healthsync.entity.*;

public class AppointmentDTO {
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer duration;
    private String type;
    private String status;
    private String notes;
    private String diagnosis;
    private String treatmentPlan;

    // Getters and Setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }
}package com.v322.healthsync.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedDTO {

    private String bedId;
    private String departmentId;
    private String departmentName;
    private Boolean isOccupied;
    private String patientId;
    private String patientName;
    private BigDecimal dailyRate;

    // Getters & Setters
    public String getBedId() { return bedId; }
    public void setBedId(String bedId) { this.bedId = bedId; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Boolean getIsOccupied() { return isOccupied; }
    public void setIsOccupied(Boolean isOccupied) { this.isOccupied = isOccupied; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }
}
package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDTO {
    private String billId;
    private String patientId;
    private String patientName;
    private LocalDate billDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String status;
    private List<BillItemDTO> billItems;

    // getters and setters
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<BillItemDTO> getBillItems() { return billItems; }
    public void setBillItems(List<BillItemDTO> billItems) { this.billItems = billItems; }
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillItemDTO {
    private String itemId;
    private String billId;
    private String description;
    private BigDecimal totalPrice;
    private Integer quantity;
    private BigDecimal unitPrice;

    // getters and setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}package com.v322.healthsync.dto;

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
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDTO {
    private String departmentId;
    private String name;
    private String location;
    private Integer totalDoctors;
    private Integer totalBeds;
    private Integer availableBeds;

    // getters and setters
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getTotalDoctors() { return totalDoctors; }
    public void setTotalDoctors(Integer totalDoctors) { this.totalDoctors = totalDoctors; }
    public Integer getTotalBeds() { return totalBeds; }
    public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }
    public Integer getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(Integer availableBeds) { this.availableBeds = availableBeds; }
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailabilityDTO {
    private String slotId;
    private String doctorId;
    private String doctorName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String dayOfWeek;

    // getters and setters
    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getDayOfWeek() { return dayOfWeek; }
}package com.v322.healthsync.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;
import com.v322.healthsync.entity.*;

// ==================== DOCTOR DTO ====================
public class DoctorDTO {
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String specialization;
    private String qualification;
    private BigDecimal consultationFee;
    private String departmentId;
    private String departmentName;
    // No collections

    // Getters and Setters
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationDTO {
    private String medicationId;
    private String name;
    private String genericName;
    private String manufacturer;
    private String description;
    private BigDecimal unitPrice;
    private String pharmacyId;
    private String pharmacyLocation;

    // getters and setters
    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getPharmacyLocation() {
        return pharmacyLocation;
    }

    public void setPharmacyLocation(String pharmacyLocation) {
        this.pharmacyLocation = pharmacyLocation;
    }
}package com.v322.healthsync.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;
import com.v322.healthsync.entity.*;

// ==================== PATIENT DTO ====================
public class PatientDTO {
    private String personId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String contactNumber;
    private String email;
    private String city;
    private LocalDate registrationDate;
    private String notes;
    // No collections - avoiding recursion

    // Getters and Setters
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacistDTO {
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String pharmacyId;
    private String pharmacyLocation;

    // getters and setters
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getPharmacyLocation() {
        return pharmacyLocation;
    }

    public void setPharmacyLocation(String pharmacyLocation) {
        this.pharmacyLocation = pharmacyLocation;
    }
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyDTO {
    private String pharmacyId;
    private String location;
    private String pharmacistId;
    private String pharmacistName;
    private Integer totalMedications;

    // getters and setters
    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(String pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public String getPharmacistName() {
        return pharmacistName;
    }

    public void setPharmacistName(String pharmacistName) {
        this.pharmacistName = pharmacistName;
    }

    public Integer getTotalMedications() {
        return totalMedications;
    }

    public void setTotalMedications(Integer totalMedications) {
        this.totalMedications = totalMedications;
    }
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {
    private String prescriptionId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDate dateIssued;
    private String status;
    private String instructions;
    private List<PrescriptionItemDTO> prescriptionItems;

    // getters and setters
    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalDate getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(LocalDate dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<PrescriptionItemDTO> getPrescriptionItems() {
        return prescriptionItems;
    }

    public void setPrescriptionItems(List<PrescriptionItemDTO> prescriptionItems) {
        this.prescriptionItems = prescriptionItems;
    }
}package com.v322.healthsync.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemDTO {
    private String prescriptionItemId;
    private String prescriptionId;
    private String medicationId;
    private String medicationName;
    private String genericName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    // getters and setters
    public String getPrescriptionItemId() {
        return prescriptionItemId;
    }

    public void setPrescriptionItemId(String prescriptionItemId) {
        this.prescriptionItemId = prescriptionItemId;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(String medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}package com.v322.healthsync.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.List;
import com.v322.healthsync.entity.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceptionistDTO {
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    // No collections

    // Getters and Setters
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}