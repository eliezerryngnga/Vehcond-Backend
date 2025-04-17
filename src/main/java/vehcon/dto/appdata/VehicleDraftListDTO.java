package vehcon.dto.appdata;

import java.time.LocalDate;

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
    private String vehicleDescription;  
    private LocalDate purchaseDate;     
    private Integer depreciatedValue;   
    private Integer totalKmsLogged;     
    private boolean mviReportsAvailable;
    private boolean anyCasePending;     
}
