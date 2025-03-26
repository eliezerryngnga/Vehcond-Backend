package vehcon.services.auth;

import java.io.IOException;
import java.util.Date;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.config.JwtService;
import vehcon.logs.Login;
import vehcon.logs.LoginRepository;
import vehcon.services.appdata.CoreServices;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutSuccessHandler{
	
	private final JwtService jwtService;
	private final LoginRepository loginRepo;
	private final CoreServices coreServices;
	
	@Auditable
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
			throws IOException, ServletException{
		
		final String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			
			String username = jwtService.extractUsername(authHeader.substring(7));
			jwtService.invalidateToken(username, coreServices.getClientIp(request));
			SecurityContextHolder.clearContext();
	
			Login login = Login.builder().username(username).uri(request.getRequestURI()).httpMethod(request.getMethod())
					.ts(new Date()).httpStatus(response.getStatus()).build();
			loginRepo.save(login);
			MDC.put("username", username);
			log.info("Logout");
			MDC.remove(username);
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().flush();
	}
}
