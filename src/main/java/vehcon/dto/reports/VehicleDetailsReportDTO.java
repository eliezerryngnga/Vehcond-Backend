package vehcon.dto.reports;


import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class VehicleDetailsReportDTO {
	
	private String departmentname;
	private String officerdesignation;
	private String fullLocation;
	
	private String directorateLetterNo;
	private Date directorateLetterDate;
	
	private String govtLetterNo;
	private Date govtLetterDate;
	
	private String vehicletypedescription;
	private String vehicledescription;
	private String vehiclemanufacturername;
	private String districtname;
	private String registrationno;
	private String engineno;
	private String chassisno;
	private Integer manufactureyear;
	private Date purchasedate;
	private Integer vehicleprice;
	private Integer totalkms;
	private Integer depreciatedamount;
	private String improvements;
	private Integer expenses;
	private Integer repairexpenses;
	private Integer repairslastsixmonths;
	private String whetheraccident;
	private String accidentcaseresolved;
	private String comments;
	
	private String mvireportavailable;
	private String battery;
	private String tyres;
	private String accidentdamage;
	private Integer mviprice;
	private String mviremarks;
	
	private List<VehiclePartsConditionReportDTO> parts;
}
