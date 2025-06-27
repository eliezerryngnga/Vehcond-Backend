package vehcon.dto.appdata;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDraftListDTO 
{
	private String applicationCode;
    private String registrationNo;
    private String departmentName;
    private String vehicleDescription;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate purchaseDate;     
    
    private Integer depreciatedValue;   
    private Integer totalKmsLogged;     
    private boolean mviReportsAvailable;
    private boolean anyCasePending;   
    
    private Integer processcode;
    
    //Vcctc Table
    private Integer tcPrice;
    
    //AllotVehicle Table
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate allotteddate;
    private String allotteesname;
    private String allotteesaddress;
    private String allotmentLetterNo;
    private Date allotmentLetterDate;

    //Lifter Table
    private String liftersname;
    private String liftersaddress;
    
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate lifteddate;
    
    //Scrap Table
    private Integer scrapAmount; 
}
