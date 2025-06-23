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
@Table(name = "tendervehicles", schema = "vehiclecondemnations")
public class TenderVehicles 
{
	@Id
	@Column(name = "applicationcode", length = 10)
	private String applicationCode;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "applicationcode", referencedColumnName = "applicationcode")
	private VehicleFinal vehicleFinal;
	
	@Column(name = "letternodate", length = 100)
	private String letternodate;
	
	@Column(name = "entrydate")
	private LocalDate entryDate;
	
	private Integer slno;
}
