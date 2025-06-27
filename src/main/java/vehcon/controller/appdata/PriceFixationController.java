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
@RequestMapping("/transport-price-fixation")
@RequiredArgsConstructor
@Slf4j
public class PriceFixationController {
	
	private final TransportService transportService;
	
	@GetMapping("/placed-before-vcc")
	public ResponseEntity<?> getVehiclePlacedBeforeVcc(
			@RequestParam(value = "search", required = false) String searchTerm,
			@RequestParam(required = false) Integer year, 
			@RequestParam(required = false) Integer month,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try
		{
			Page<VehicleDraftListDTO> placedVehicles = transportService.getVehiclesPlacedBeforeVcc(
					searchTerm, 
					year, 
					month,
					pageable,
					user);
			return ResponseEntity.ok(placedVehicles);
		}
		catch(Exception e)
		{
			log.error("An error occurred while fetching: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occured while retrieving Placed Vehicles");
		}
	}
	
	@PostMapping("/fixing-price")
	public ResponseEntity<?> priceFixationByTc(
			@Valid @RequestBody PlacedBeforeVccRequestDTO placeBeforeVccDto
			)
	{
		log.info("Received /fixing-price request with DTO: {}", placeBeforeVccDto);
		try
		{
			VehicleFinal priceFixation = transportService.priceFixationByTc(placeBeforeVccDto);
			return ResponseEntity.ok(priceFixation);
		}
		catch(EntityNotFoundException enfEx)
		{
			 log.warn("Price fixation failed for DTO {}: {}", placeBeforeVccDto, enfEx.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(enfEx.getMessage());
		}
		catch(Exception ex)
		{
			 log.error("Unexpected error during price fixation for DTO {}: {}", placeBeforeVccDto, ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occured while placing the vehicle before VCC");
		}
	}
	
}
