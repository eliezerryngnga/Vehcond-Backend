package vehcon.dto.reports;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VehicleReportsDTO {
	
	private Long slno;
	private String applicationcode;
	
	private String departmentname;
	private String registrationno;
	
	private String officename;
	
	private String vehicletypedescription;
	
	private Integer manufactureyear;
	
	private LocalDate purchasedate;
	
	private Integer vehicleprice;
	private Integer totalkms;
	private Integer depreciatedamount;
	private String accidentcaseresolved;
	private String mvireportavailable;
	private String remarks;
	
}
