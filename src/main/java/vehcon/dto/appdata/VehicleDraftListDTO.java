package vehcon.dto.appdata;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleDraftListDTO 
{
	private String applicationCode;     // From VehicleDraft.applicationCode
    private String registrationNo;      // From VehicleDraft.registrationNo
    private String vehicleDescription;  // From VehicleDraft.vehicledescription
    private LocalDate purchaseDate;     // From VehicleDraft.purchasedate
    private Integer depreciatedValue;   // From VehicleDraft.depreciatedamount
    private Integer totalKmsLogged;     // From VehicleDraft.totalkms
    private boolean mviReportsAvailable;// Derived from VehicleDraft.mvireportavailable
    private boolean anyCasePending;     // Derived from VehicleDraft.whetheraccident (as per your current mapping)
}
