package vehcon.dto.appdata;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PlacedBeforeVccRequestDTO {

	private String applicationCode;
	private String registrationNo;
	
	private String priceApproved;
	
	private Integer price;
	
	private String vccLetterNo;
	private LocalDate vccLetterDate;
	
}
