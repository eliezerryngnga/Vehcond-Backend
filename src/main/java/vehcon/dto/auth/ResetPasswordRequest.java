package vehcon.dto.auth;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResetPasswordRequest {
	
	private UUID user_id;
	
	private String username;

	@NotBlank(message = "Token is required")
	private String token;
	
	private String password;

}
