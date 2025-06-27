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
import vehcon.dto.appdata.LiftingDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.auth.User;
import vehcon.services.appdata.TransportService;

@RestController
@RequestMapping("/transport-lifting")
@RequiredArgsConstructor
@Slf4j
public class LiftingController {
	
	private final TransportService transportService;
	
	@GetMapping("/allotted-list")
	public ResponseEntity<?> allottedVehicles(
			@RequestParam(value ="search",required = false) String searchTerm,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try
		{
			Page<VehicleDraftListDTO> vehicles = transportService.getAllottedVehicles(searchTerm, year, month, pageable, user);
			return ResponseEntity.ok(vehicles);
		}
		catch(Exception e)
		{
			log.error("An error occurred while retrieving the vehicles", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occured while retrieving Allotted Vehicles");
		}
	}
	
	@PostMapping("/lift")
	public ResponseEntity<?> liftingVehicleProcess(@Valid @RequestBody LiftingDTO dto)
	{
		try
		{
			VehicleFinal updatedVehicle = transportService.handleVehicleLifting(dto);
			
			return ResponseEntity.ok(updatedVehicle);
		}
		catch(EntityNotFoundException e)
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
		}
	}
	
	@GetMapping("/lifted-vehicles")
	public ResponseEntity<?> getLiftedVehicles(
			@RequestParam(value = "search", required = false) String searchTerm,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false ) Integer month,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try
		{
			Page<VehicleDraftListDTO> liftedVehicles = transportService.getLiftedVehicles(searchTerm, year, month, pageable, user);
			return ResponseEntity.ok(liftedVehicles);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred while retrieving Lifted Vehicles");
		}
	}

}
