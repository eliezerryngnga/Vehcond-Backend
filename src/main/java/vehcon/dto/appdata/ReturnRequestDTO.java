package vehcon.dto.appdata;

import lombok.Data;

@Data
public class ReturnRequestDTO {

	private String applicationCode;
	
	private String registrationNo;
	
	//@NotBlank(message="Remarks are mandatory when returning a vehicle")
	private String remarks;
}
