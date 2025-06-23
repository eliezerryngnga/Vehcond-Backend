package vehcon.dto.appdata;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AllottVehiclesDTO {
	
	private String applicationCode;
	private String registrationNo;
	private String letterNo;
	private LocalDate letterDate;
	private LocalDate allotmentDate;
	private String allotteesName;
	private String allotteesAddress;
}
