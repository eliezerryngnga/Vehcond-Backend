package vehcon.controller.appdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vehcon.dto.appdata.PlacedBeforeVccRequestDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.auth.User;
import vehcon.services.appdata.TransportService;

@RestController
@RequestMapping("/transport-placement")
@RequiredArgsConstructor
@Slf4j
public class TransportPlacementController {
	
	private final TransportService transportService;
	
	@GetMapping("/list")
	public ResponseEntity<?> getApprovedVehicles(
			@RequestParam(value="search", required = false) String searchTerm,
			@RequestParam(required = false) Integer year, 
			@RequestParam(required = false) Integer month,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try {
			Page<VehicleDraftListDTO> approvedVehicles = transportService.getApprovedVehicles(
					searchTerm,
					year,
					month,
					pageable,
					user
					);
			
			
			return ResponseEntity.ok(approvedVehicles);
		}
		
		catch(Exception e)
		{
			log.error("An error occurred while fetching: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred while retrieving Approved Vehilces");
		}
	}
	
	@PostMapping("/place-before-vcc")
	public ResponseEntity<?> placeBeforeVcc(
			
			@Valid @RequestBody PlacedBeforeVccRequestDTO placeBeforeVccDto
			)
	{
		try {
			VehicleFinal beforeVcc = transportService.placeVehicleBeforeVcc( placeBeforeVccDto);
			return ResponseEntity.ok(beforeVcc);
		}
		catch(EntityNotFoundException enfEx)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(enfEx.getMessage());
		}
		catch(Exception ex)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occured while placing the vehicle before VCC");
		}
	}
}
