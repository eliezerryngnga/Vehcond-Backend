package vehcon.models.vehiclecondemnations;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VehiclePartsConditionDraftId implements Serializable {
    private String applicationcode;
    private Integer vehiclepartcode;
}