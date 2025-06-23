package vehcon.dto.reports;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AllotmentLetterDTO {
	
	private String allotteesname;
	private String allotteesaddress;
	private String letterNumber;
	private LocalDate letterDate;
	
	private String registrationno;
	private String officename;
	private String locations;
	private String departmentname;
	
	private Integer vcctcprice;
}
