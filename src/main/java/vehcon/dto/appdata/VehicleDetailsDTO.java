package vehcon.dto.appdata;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class VehicleDetailsDTO {

	private String applicationCode;
	
    private String departmentName;
    
    private String officeName;
    
    private String officerDesignation;

    private String premises;
    
    private String address1;
    
    private String address2;
    
    private String directorateLetterNo;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate directorateLetterDate;
    
    private String forwardingLetterNo;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate govForwardingLetterDate;
    
    private String vehicletypename;

    private String vehicledescription;

    private Integer vehiclemanufacturercode;

    private Integer registeredDistrict;

//    private String registrationNo;

    private String rtoCode;
    
    private String vehicleRegistrationNumber;
    
    private String engineno;

    private String chassisno;

    private Integer manufactureyear;

    @JsonFormat(pattern = "dd-MM-yyyy")
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
    
//    private Integer financialYearCode;
//    private Integer financialYearFrom;
//    private Integer financialYearTo;
    
    
    	private String financialYear;
    
    private List<PartsConditionInputDTO> vehiclePartsFinal;
}
