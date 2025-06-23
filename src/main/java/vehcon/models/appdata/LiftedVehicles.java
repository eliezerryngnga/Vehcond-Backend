package vehcon.models.appdata;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "liftedvehicles", schema = "vehiclecondemnations")
public class LiftedVehicles {
	
	@Id
	@Column(name = "applicationcode", length = 10)
	private String applicationCode;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "applicationcode", referencedColumnName = "applicationcode")
	private VehicleFinal vehicleFinal;
	
	@Column(name = "letternodate", length = 100)
	private String letterNoDate;
	
	private LocalDate lifteddate;
	
	@Column(name = "liftersname", length = 100)
	private String liftersName;
	
	@Column(name = "liftersaddress")
	private String liftersAddress;
	
	@Column(name = "liftedmode", length = 1)
	private String liftedMode;
	
	@Column(name = "entrydate")
	private LocalDate entryDate;
}
