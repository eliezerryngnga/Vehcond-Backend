package vehcon.dto.reports;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class VehicleDetailsDTO {
	
	private String departmentname;
	private String officerdesignation;
	private String premises;
	private String locations;
	
	private String directorateLetterNo;
	private LocalDate directorateLetterDate;
	
	private String govtLetterNo;
	private LocalDate govtLetterDate;
	
	private String vehicletypedescription;
	private String vehicledescription;
	private String vehiclemanufacturername;
	private String districtname;
	private String registrationno;
	private String engineno;
	private String chassisno;
	private Integer manufactureyear;
	private LocalDate purchasedate;
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
	
	private List<VehiclePartsConditionDTO> parts;
}
