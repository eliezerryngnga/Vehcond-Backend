package vehcon.models.vehiclecondemnations;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VehiclePartsConditionDraftId implements Serializable {
    private String applicationCode;
    private Integer vehiclepartcode;
}