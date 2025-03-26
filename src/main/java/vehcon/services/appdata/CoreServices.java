package vehcon.services.appdata;

import java.util.Map;
import java.util.Optional;

import static vehcon.models.auth.Role.ADMIN;
import static vehcon.models.auth.Role.TD;
import static vehcon.models.auth.Role.ADMINISTRATOR;
import static vehcon.models.auth.Role.DA;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vehcon.exception.ObjectNotFoundException;
import vehcon.models.auth.User;
import vehcon.repo.auth.UserRepository;

@Service
@RequiredArgsConstructor
public class CoreServices {
	

	private final UserRepository userRepo;
	
	public String getClientIp(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
	
	public String convertEmail(String mail) {
		String email="";
		int indexOfAt = mail.indexOf("@");
		if (indexOfAt != -1) {
			email = mail.substring(0, indexOfAt + 1) // Include "@" in the substring
					+ mail.substring(indexOfAt + 1).replace(".", "[dot]");
			email = email.replace("@", "[at]");
		}
		return email;
	}
	
	public Map<String,Object> isValidFilename(String originalFileName) {
		Map<String, Object> responseMap = new HashMap<>();
		String metaCharactersRegex = "[\\p{Punct}&&[^._()\\-]]";
		String[] parts = originalFileName.split("\\.");
		
		if (originalFileName.contains("..")) {
			responseMap.put("detail", "Sorry! File name is containing invalid path sequence: " + originalFileName);
			responseMap.put("status", false);
		}
		else if (originalFileName.length() > 255) {
			responseMap.put("detail", "Sorry! File name is too long");
			responseMap.put("status", false);
		}
		
		else if (originalFileName.matches(".*" + metaCharactersRegex + ".*") || originalFileName.contains("%00")) {
			responseMap.put("detail", "Sorry! File name contains invalid characters");
			responseMap.put("status", false);
		}
		
		else if (parts.length > 2) {
			responseMap.put("detail", "Sorry! File name has double extension");
			responseMap.put("status", false);
		}
		else {
			responseMap.put("detail", "Accepted");
			responseMap.put("status", true);
		}
		return responseMap;
	}
	
	public String getRoleName(String username) {
		try {
			String roleName="-";
			
			Optional<User> user = userRepo.findByUsername(username);
			if(user.isPresent()) {
				if(user.get().getRole().equals(ADMINISTRATOR))
					roleName="Administrator";
				else if(user.get().getRole().equals(DA))
					roleName="Dealing Asst.";
				else if(user.get().getRole().equals(TD))
					roleName="Transport Department";
				else if(user.get().getRole().equals(ADMIN))
					roleName="Admin";
			}
			
			return roleName;
		}catch(Exception ex) {
			throw ex;
		}
	}

}
