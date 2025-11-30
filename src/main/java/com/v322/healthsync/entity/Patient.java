
// ==================== PATIENT ENTITY ====================
package com.v322.healthsync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("PATIENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends User {
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(length = 100)
    private String notes;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Prescription> prescriptions;

    @OneToOne(mappedBy = "patient")
    private Bed bed;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Bill> bills;

    // getters and setters
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    public List<Appointment> getAppointments() {
        return appointments;
    }
    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }
    public Bed getBed() {
        return bed;
    }
    public List<Bill> getBills() {
        return bills;
    }
}
