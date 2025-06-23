package vehcon.dto.appdata;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TenderVehiclesDTO {

	private String applicationCode;
	
	private String registrationNo;
	
	private String letterNo;
	
	private LocalDate letterDate;	
}
