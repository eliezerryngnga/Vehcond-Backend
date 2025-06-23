package vehcon.dto.appdata;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LiftingDTO {
	
	private String applicationCode;
	
	private String registrationNo;
	
	private String liftMode; // Y for Yes or N for No
	
	private String liftersName;
	
	private String liftersAddress;
	
	private String forwardingLetterNo;
	
	private LocalDate forwardingLetterDate;
	
	private LocalDate liftedDate;
}
