package vehcon.services.auth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import javax.crypto.Cipher;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.annotations.Auditable;
import vehcon.config.JwtService;
import vehcon.dto.auth.AuthenticationRequest;
import vehcon.dto.auth.AuthenticationResponse;
import vehcon.dto.auth.RegisterRequest;
import vehcon.exception.InternalServerError;
import vehcon.exception.ObjectNotFoundException;
import vehcon.exception.UnauthorizedException;
import vehcon.logs.Login;
import vehcon.logs.LoginRepository;
import vehcon.models.auth.PasswordResetToken;
import vehcon.models.auth.Role;
import vehcon.models.auth.Roles;
import vehcon.models.auth.User;
import vehcon.repo.auth.PasswordTokenRepository;
import vehcon.repo.auth.RolesRepository;
import vehcon.repo.auth.UserRepository;
import vehcon.services.appdata.CoreServices;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final CoreServices coreServices;
	private final PasswordTokenRepository passwordTokenRepo;
	private final LoginRepository loginRepo;
	private final RolesRepository rolesRepo;
	
	@Value("${keys.dir}")
	private String keysDir;
	
	@Auditable
	@Transactional
	public User register(RegisterRequest request) {
		
		Optional<User> userr = userRepo.findByUsername(request.getUsername());

		if (userr.isPresent())
			throw new InternalServerError("Username already registered");
		
		Role role = Role.valueOf(request.getRole().toString().toUpperCase());

		//String pw = request.getPassword() == null ? "password" : decryptPassword(request.getPassword());			
		String pw =request.getPassword();
		var user = User.builder().name(request.getName())
				.departmentCode(request.getDepartmentCode())
				.username(request.getUsername()).password(passwordEncoder.encode(pw))
				//.roleCode(request.getRoleCode())
				.role(role).userAccess(true).build();
		
		userRepo.save(user);
		
		return user;
	}

	@Auditable
	public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpRequest, HttpServletResponse response)
			throws BadCredentialsException, UsernameNotFoundException, IOException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
					decryptPassword(request.getPassword())));
		} catch (BadCredentialsException exception) {
			throw new UnauthorizedException("Incorrect username or password");
		} catch (UsernameNotFoundException exception) {
			throw new UnauthorizedException("Incorrect username or password");
		} catch (DisabledException exception) {
			throw new UnauthorizedException("Account is disabled");
		}
		var user = userRepo.findByUsername(request.getUsername()).orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		
		Login login = Login.builder().username(user.getUsername()).uri(httpRequest.getRequestURI()).httpMethod(httpRequest.getMethod())
				.ts(new Date()).httpStatus(response.getStatus()).build();
		loginRepo.save(login);
		
		
		MDC.put("username", user.getUsername());
		log.info("Login");
		MDC.remove("username");
		return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken)
				.role(user.getRole().toString()).roleCode(getRoleCode(user.getRole().toString()))
				.build();
	}

	@Auditable
	public AuthenticationResponse authenticate2(AuthenticationRequest request, HttpServletRequest httpRequest, HttpServletResponse response)
			throws BadCredentialsException, UsernameNotFoundException, IOException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
					request.getPassword()));
		} catch (BadCredentialsException exception) {
			throw new UnauthorizedException("Incorrect username or password");
		}catch (UsernameNotFoundException exception) {
			throw new UnauthorizedException("Incorrect username or password");
		} catch (DisabledException exception) {
			throw new UnauthorizedException("Account is disabled");
		}
		var user = userRepo.findByUsername(request.getUsername()).orElseThrow(()->new ObjectNotFoundException("User not found"));
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		
		Login login = Login.builder().username(user.getUsername()).uri(httpRequest.getRequestURI()).httpMethod(httpRequest.getMethod())
				.ts(new Date()).httpStatus(response.getStatus()).build();
		loginRepo.save(login);
		
		MDC.put("username", user.getUsername());
		log.info("Login");
		MDC.remove("username");
		
		return AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken)
				.role(user.getRole().toString()).roleCode(getRoleCode(user.getRole().toString())).build();
	}
	
	public String decryptPassword(String encryptedPassword) {

		try {
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");
			File privateKeyFile = new File(keysDir + "//private.key");
			byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrypt = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));

			return new String(decrypt);
		} catch (Exception e) {
			throw new InternalServerError("Failed to decrypt password", e);
		}
	}
	
	@Transactional
	public void refreshToken2(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			var user = this.userRepo.findByUsername(userEmail).orElseThrow(()-> new ObjectNotFoundException("User not found"));
			if (jwtService.isTokenValid(refreshToken, user, coreServices.getClientIp(request))) {
				var accessToken = jwtService.generateToken(user);
				var authResponse = AuthenticationResponse.builder().accessToken(accessToken).role(user.getRole().toString())
						.refreshToken(refreshToken).build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			} else 
				throw new UnauthorizedException("Invalidated Token");
		}
	}
	
	@Transactional
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			var user = this.userRepo.findByUsername(userEmail).orElseThrow(()-> new ObjectNotFoundException("User not found"));
			if (jwtService.isTokenValid(refreshToken, user,coreServices.getClientIp(request))) {
				var accessToken = jwtService.generateToken(user);
				jwtService.invalidateRefreshToken(refreshToken);
				var authResponse = AuthenticationResponse.builder().accessToken(accessToken).role(user.getRole().toString())
						.refreshToken(jwtService.generateRefreshToken(user)).build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			} else 
				throw new UnauthorizedException("Invalidated Token");
		}
	}

	private void createKeys() {
		try {
			Files.createDirectories(Paths.get(keysDir).toAbsolutePath().normalize());

			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			try (FileOutputStream fos = new FileOutputStream(keysDir + "//public.key")) {
				fos.write(publicKey.getEncoded());
			}
			try (FileOutputStream fos = new FileOutputStream(keysDir + "//private.key")) {
				fos.write(privateKey.getEncoded());
			}
		} catch (Exception e) {
			throw new InternalServerError("Failed to generate Key Pair for encryption", e);
		}
	}

	public String getPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		// Here check the file if it has the keys or not. If not then generate new
		File publicKeyFile = new File(keysDir + "//public.key");
		byte[] publicKeyBytes;
		if (publicKeyFile.exists() && !publicKeyFile.isDirectory()) {
			publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
		} else {
			createKeys();
			publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
		}
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	public String validatePasswordResetToken(String token) {
	    Optional<PasswordResetToken> passToken = passwordTokenRepo.findByToken(token);

	    if(passToken.isEmpty())
	    	return "invalidToken";
	    else if(isTokenExpired(passToken.get()))
	    	return "expired";
	    else {
	    	passwordTokenRepo.deleteById(passToken.get().getId());
	    	return null;
	    }
	}

	private boolean isTokenExpired(PasswordResetToken passToken) {
	    final Calendar cal = Calendar.getInstance();
	    return passToken.expiry.before(cal.getTime());
	}
	
	private Integer getRoleCode(String role) {
		Roles role1 = rolesRepo.findByRole(role).orElseThrow();
		return role1.getRolecode();
	}

}
