package vehcon.dto.auth;

import lombok.Data;

@Data
public class UserListDTO {
	
	private Integer usercode;
	private String username;
	private String name;
	private Integer departmentCode;
	private String departmentName;
	
	private String roleName;
	private String roleDescription;
	
	private boolean useraccess;

}
