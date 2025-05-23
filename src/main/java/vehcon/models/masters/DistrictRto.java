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
@Table(name="districtrtos", schema="master")
public class DistrictRto
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "districtrtocode")
	private Integer districtRtoCode;
	
	@Column(name = "rtocode")
	private String rtoCode;
	
	@Column(name = "districtcode")
	private Integer districtCode;
}