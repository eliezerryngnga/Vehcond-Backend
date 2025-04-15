package vehcon.models.appdata;

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
public class VehiclePartsConditionFinalId implements Serializable {
    private String applicationcode;
    private Integer vehiclepartcode;
}