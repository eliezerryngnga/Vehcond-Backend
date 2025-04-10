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
@Table(name = "vehicletypes", schema="master")
public class VehicleType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vehicletypecode")
	private Integer vehicleTypeCode;
	
	@Column(name = "vehicletypedescription")
	private String vehicletypedescription;
}
