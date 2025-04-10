package vehcon.dto.appdata;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class VehicleDraftDTO {
	
    private Integer departmentCode;
    
    private String officeName;
    
    private String officerDesignation;

    private String premises;
    
    private String address1;
    
    private String address2;
    
    private String directorateLetterNo;
    
    private LocalDate directorateLetterDate;
    
    private String forwardingLetterNo;
    
    private LocalDate govForwardingLetterDate;
    
    private Integer vehicletypecode;

    private String vehicledescription;

    private Integer vehiclemanufacturercode;

    private Integer registeredDistrict;

//    private String registrationNo;

    private String rtoNo;
    
    private String vehicleRegistrationNumber;
    
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
    
    private Integer financialYearCode;
    
//	private List<VehiclePartsConditionDraft> vehiclePartsDraft;
    
    private List<PartsConditionInputDTO> vehiclePartsDraft;

}
