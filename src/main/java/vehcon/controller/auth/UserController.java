package vehcon.controller.auth;

import static vehcon.models.auth.Role.DA;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vehcon.annotations.Auditable;
import vehcon.dto.auth.ChangePasswordRequest;
import vehcon.dto.auth.RegisterRequest;
import vehcon.exception.InternalServerError;
import vehcon.exception.ObjectNotFoundException;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
import vehcon.repo.auth.UserRepository;
import vehcon.services.appdata.CoreServices;
import vehcon.services.auth.AuthenticationService;

@RestController
@RequestMapping(path = "/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {
	
	private final CoreServices coreService;
	private final UserRepository userRepo;
	private final AuthenticationService authService;
	private final PasswordEncoder passwordEncoder;
	
	@GetMapping("/profile")
	public ResponseEntity<Map<String, Object>> getuserInfo(@AuthenticationPrincipal User user) {
		try {
			Map<String, Object> userInfo = new HashMap<>();

			userInfo.put("username", user.getUsername());
			userInfo.put("role", user.getRole());
			userInfo.put("name", user.getName());
			userInfo.put("departmentCode", user.getDepartmentCode());

			return new ResponseEntity<>(userInfo, HttpStatus.OK);

		} catch (Exception ex) {
			throw new InternalServerError("Unable to fetch user information", ex);
		}
	}
	
	@Auditable
	@PostMapping("/change-password")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request,
			@AuthenticationPrincipal User user, HttpServletRequest httpRequest) {
		
		Map<String, String> map = new HashMap<>();
		User u = userRepo.findByUsername(user.getUsername()).orElseThrow(()->new ObjectNotFoundException("Invalid username"));
		try {

			if (!(passwordEncoder.matches(authService.decryptPassword(request.getOldPassword()), u.getPassword())))
				throw new UnauthorizedException("Incorrect password");
			
			u.setPassword(passwordEncoder.encode(authService.decryptPassword(request.getNewPassword())));
			//u.get().setUpdatedBy(user.getUsername());
			userRepo.save(u);

			map.put("message", "Password Changed Successfully");

			return new ResponseEntity<>(map, HttpStatus.OK);

		} catch (ObjectNotFoundException | UnauthorizedException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to change password", ex);
		}
	}
	
	@Auditable
	@Transactional
	@PostMapping("/update")
	public ResponseEntity<Map<String, String>> update(@Valid @RequestBody RegisterRequest request, @AuthenticationPrincipal User user,
			HttpServletRequest httpRequest) {
		try {
			Map<String, String> map = new HashMap<>();
			
			User u = userRepo.findByUsername(user.getUsername()).orElseThrow(()->new ObjectNotFoundException("Invalid username"));
			
			u.setDepartmentCode(request.getDepartmentCode());
			u.setName(request.getName());

			map.put("detail", "Profile updated.");

			return new ResponseEntity<>(map, HttpStatus.OK);

		} catch (UnauthorizedException|InternalServerError ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalServerError("Unable to update profile", ex);
		}
	}
}
