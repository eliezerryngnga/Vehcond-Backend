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
@Table(name="districts", schema="master")
public class Districts {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "districtcode")
	private Integer districtCode;
	
	@Column(name = "districtname")
	private String districtName;
	
	@Column(name = "lgdcode")
	private Integer lgdCode;
}
