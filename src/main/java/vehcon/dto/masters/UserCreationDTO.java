package vehcon.dto.masters;

import lombok.Data;

@Data
public class UserCreationDTO {
	private String username;
	private String password;
	private String name;
	private Integer departmentCode;
	private boolean useraccess;
	private String role;
}