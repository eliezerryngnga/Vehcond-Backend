package vehcon.models.vehiclecondemnations;

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
@Table(name = "vehiclepartsconditions_draft", schema="vehiclecondemnations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(VehiclePartsConditionDraftId.class)
public class VehiclePartsConditionDraft {

 @Id
 @ManyToOne
 @JoinColumn(name = "applicationcode", referencedColumnName = "applicationcode")
 private VehicleDraft applicationcode;

 @Id
 @ManyToOne
 @JoinColumn(name = "vehiclepartcode", referencedColumnName = "vehiclepartcode")
 private VehicleParts vehiclepartcode;

 @Column(name = "condition")
 private String condition;

 @Column(name = "slno")
 private Integer slno;
}