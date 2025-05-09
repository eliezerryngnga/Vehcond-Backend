package vehcon.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public class AuthenticationResponse {
	
	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("refresh_token")
	private String refreshToken;

	@JsonProperty("role")
	private String role;
	
	@JsonProperty("roleCode")
	private Integer roleCode;
}
