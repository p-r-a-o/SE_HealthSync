
// ==================== PHARMACY ENTITY ====================
package com.v322.healthsync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Entity
@Table(name = "pharmacy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pharmacy {

    @Id
    @Column(name = "pharmacy_id", length = 50)
    private String pharmacyId;

    @Column(length = 100)
    private String location;

    @OneToOne(mappedBy = "pharmacy")
    private Pharmacist pharmacist;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL)
    private List<Medication> medications;

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
    public Pharmacist getPharmacist() {
        return pharmacist;
    }

    public void setPharmacist(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }
    public List<Medication> getMedications() {
        return medications;
    }
}
