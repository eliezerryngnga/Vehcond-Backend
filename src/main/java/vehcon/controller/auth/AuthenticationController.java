package vehcon.controller.auth;

import static vehcon.models.auth.Role.DA;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vehcon.dto.auth.AuthenticationRequest;
import vehcon.dto.auth.AuthenticationResponse;
import vehcon.dto.auth.RegisterRequest;
import vehcon.exception.InternalServerError;
import vehcon.exception.UnauthorizedException;
import vehcon.services.auth.AuthenticationService;
import vehcon.services.auth.CaptchaService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	
	private final AuthenticationService authService;
	private final CaptchaService captchaService;
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws BadCredentialsException, UsernameNotFoundException, IOException {
//		CaptchaEntry captcha = captchaStore.get(request.getUuid().toString());
//	        
//	        System.out.println(captcha != null && captcha.getCaptcha().equals(request.getCaptcha()));
//	        System.out.println("User captcha is: "+request.getCaptcha()+" and the captcha store captcha is: "+captcha);
//	        System.out.println("The captcha Store is:"+captchaStore);
//	        if (captcha != null && captcha.getCaptcha().equals(request.getCaptcha())) {
//	            captchaStore.remove(request.getUuid().toString());
//	        }
//	        System.out.println("The captcha Store is:"+captchaStore);
		if (captchaService.validateCaptcha(request.getCaptchaToken().toString(), request.getCaptcha())) {
			return ResponseEntity.ok(authService.authenticate(request, httpRequest, httpResponse));
		} else
			throw new UnauthorizedException("Invalid Captcha");
	}
	
	@PostMapping("/authenticate2")
	public ResponseEntity<AuthenticationResponse> authenticate2(@Valid @RequestBody AuthenticationRequest request,
			HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws BadCredentialsException, UsernameNotFoundException, IOException {
//		CaptchaEntry captcha = captchaStore.get(request.getUuid().toString());
//	        
//	        System.out.println(captcha != null && captcha.getCaptcha().equals(request.getCaptcha()));
//	        System.out.println("User captcha is: "+request.getCaptcha()+" and the captcha store captcha is: "+captcha);
//	        System.out.println("The captcha Store is:"+captchaStore);
//	        if (captcha != null && captcha.getCaptcha().equals(request.getCaptcha())) {
//	            captchaStore.remove(request.getUuid().toString());
//	        }
//	        System.out.println("The captcha Store is:"+captchaStore);
		if (captchaService.validateCaptcha(request.getCaptchaToken().toString(), request.getCaptcha())) {
			return ResponseEntity.ok(authService.authenticate2(request, httpRequest, httpResponse));
		} else
			throw new UnauthorizedException("Invalid Captcha");
	}

	@GetMapping("/refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			authService.refreshToken(request, response);
		} catch (UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to refresh token.", ex);
		}
	}

	@GetMapping("/refresh-captcha")
	public ResponseEntity<Map<String, Object>> refreshCaptcha() {
		try {			
			return ResponseEntity.ok(captchaService.generateCaptcha());
		} catch (Exception ex) {
			throw new InternalServerError("Unable to refresh Captcha", ex);
		}
	}

	@GetMapping("/get-public-key")
	public ResponseEntity<Map<String, Object>> getPublicKey() {
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("publicKey", authService.getPublicKey());
			return new ResponseEntity<>(map, HttpStatus.OK);
		} catch (Exception ex) {
			throw new InternalServerError("Unable to get Public Key", ex);
		}
	}
	
	@Transactional
	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request,
			HttpServletRequest httpRequest) {
		try {
			Map<String, String> map = new HashMap<>();
			
			request.setRole(DA);
			
			authService.register(request);
			map.put("detail", "User Registered.");

			return new ResponseEntity<>(map, HttpStatus.OK);

		} catch (UnauthorizedException|InternalServerError ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to register user", ex);
		}
	}

}
