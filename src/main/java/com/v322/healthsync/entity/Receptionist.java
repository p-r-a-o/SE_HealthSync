
// ==================== RECEPTIONIST ENTITY ====================
package com.v322.healthsync.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("RECEPTIONIST")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
// @AllArgsConstructor
public class Receptionist extends User {
    // No additional fields beyond User
}
