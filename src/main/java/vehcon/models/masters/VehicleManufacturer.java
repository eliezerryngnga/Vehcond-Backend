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
@Table(name = "vehiclemanufacturers", schema = "master")
public class VehicleManufacturer {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column(name = "vehiclemanufacturercode")
	private Integer vehicleManufacturerCode;
	
	@Column(name = "vehiclemanufacturername")
	private String vehicleManufacturerName;
}
