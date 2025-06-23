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
@Table(name = "departments", schema = "master")
public class Departments {
	
	@Id
	@Column(name="departmentcode")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer departmentCode;
	
	@Column(name="departmentname")
	private String departmentName;
	
	@Column(name="departmentcode_iobs")
	private Integer departmentCode_Iobs;
}

