package vehcon.models.masters;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "vehicleparts", schema="master")
public class VehicleParts {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vehiclepartcode")
	private Integer vehiclePartCode;
	
	@Column(name = "vehiclepartdescription")
	private String vehiclePartDescription;
}
