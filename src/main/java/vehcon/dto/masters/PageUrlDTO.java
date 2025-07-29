package vehcon.dto.masters;

import lombok.Data;

@Data
public class PageUrlDTO {
	private Integer urlCode;
	
	private String processName;
	private String subProcessName;
	private String pageUrl;
}
