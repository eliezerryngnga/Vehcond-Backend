package vehcon.models.appdata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vehcon.models.masters.VehicleParts;

@Entity
@Table(name = "vehiclepartsconditions", schema="vehiclecondemnations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VehiclePartsConditionFinalId.class)
public class VehiclePartsConditionFinal {

 @Id
 @ManyToOne
 @JoinColumn(name = "applicationcode", referencedColumnName = "applicationcode")
 private VehicleFinal applicationcode;

 @Id
 @ManyToOne
 @JoinColumn(name = "vehiclepartcode", referencedColumnName = "vehiclepartcode")
 private VehicleParts vehiclepartcode;

 @Column(name = "condition")
 private String condition;

 @Column(name = "slno")
 private Integer slno;
}