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
@Table(name ="financialyear", schema="master")
public class FinancialYear {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="financialyearcode")
	private Integer financialyearcode;
	
	@Column(name ="financialyearfrom")
	private Integer financialYearFrom;
	
	@Column(name = "financialyearto")
	private Integer financialYearTo;
}