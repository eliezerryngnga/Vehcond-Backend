package vehcon.dto.reports;

import java.time.LocalDate;
import java.util.Date;

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
	
//	VCCtc Temp - letternodatefield.
	private String letterNumber;
	private Date letterDate;
	
//	Vcctc Table
	private String tcLetterNo;
	private Date tcLetterDate;
	private Integer tcvehicleprice;
	
	
//	AllotVehicle Table
	private LocalDate allotteddate;
	private String allotteesname;
	private String allotteesaddress;
	
//	Lifter Table
	private String liftersname;
	private String liftersaddress;
	private LocalDate lifteddate;
	
//	Scrap Table
	private Integer scrapAmount;
	
}
