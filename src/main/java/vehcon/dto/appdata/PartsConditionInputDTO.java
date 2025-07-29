package vehcon.dto.appdata;

import lombok.Data;

@Data
public class PartsConditionInputDTO {
	
	private Integer vehiclepartcode;
	private String vehiclepartname;
	private String condition;
}
