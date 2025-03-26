package vehcon.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import vehcon.models.auth.Role;

@Data
@Builder
public class RegisterRequest {
	
	@NotBlank(message="Username is required")
	private String username;
	
	@NotBlank
	private String name;
	
	@NotNull
	private Integer departmentCode;
	
	//private Integer roleCode;
	
	//@NotNull(message = "Role is required")
	private Role role;
	
	@NotBlank(message = "Password (password) is required")
	@Size(min = 8, message = "Password should be at least 8 characters long.")
	private String password;
}
