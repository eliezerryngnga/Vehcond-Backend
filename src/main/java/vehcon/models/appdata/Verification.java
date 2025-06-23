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
@Table(name = "verifications", schema = "vehiclecondemnations")
@NoArgsConstructor
@AllArgsConstructor
public class Verification {
	
	@Id
	@Column(name = "applicationcode", length = 10)
    private String applicationCode;
	
	@OneToOne
	@JoinColumn(name="applicationcode", referencedColumnName="applicationcode")
	@MapsId
	private VehicleFinal vehicleFinal;
	
	@Column(name = "letternodate", length = 100 )
	private String letterNoDate;
	
	@Column(name = "entrydate")
	private LocalDate entryDate;
	
	@Column(name = "slno")
	private Integer slno;
	
	@Column(name = "remarks")
	private String remarks;
}
