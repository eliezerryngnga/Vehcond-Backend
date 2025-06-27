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
import vehcon.dto.appdata.ScrapDTO;
import vehcon.dto.appdata.TenderVehiclesDTO;
import vehcon.dto.appdata.VehicleDraftListDTO;
import vehcon.models.appdata.VehicleFinal;
import vehcon.models.auth.User;
import vehcon.services.appdata.TransportService;

@RestController
@RequestMapping("/transport-action")
@RequiredArgsConstructor
@Slf4j
public class TransportTenderScrapController {
	
	private final TransportService transportService;
	
	@GetMapping("/heavy-vehicles")	
	public ResponseEntity<?> getHeavyVehicles(
			@RequestParam(required = false) String searchTerm,
			Pageable pageable
			)
	{
		try
		{
			Page<VehicleDraftListDTO> heavyVehicles = transportService.getHeavyVehiclesForTender(searchTerm, pageable);
			return ResponseEntity.ok(heavyVehicles);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occured while retrieving Heavy Vehicles");
		}
	}
	
	@PostMapping("/declare-scrap")
	public ResponseEntity<?> declareVehicleScrapByTc(@Valid @RequestBody ScrapDTO dto)
	{
		try
		{
			VehicleFinal updatedVehicle = transportService.declareVehicleScrapByTc(dto);
			
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
	
	@PostMapping("/declare-tender")
	public ResponseEntity<?> declareVehicleTenderByTc(
			@Valid @RequestBody TenderVehiclesDTO dto
			)
	{
		try
		{
			VehicleFinal updatedVehicle = transportService.declareVehicleTenderByTc(dto);
			
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
	
	@GetMapping("/tendered-vehicles")
	public ResponseEntity<?> getTenderedVehicles(
			@RequestParam(value="search", required = false) String searchTerm,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try
		{
			Page<VehicleDraftListDTO> tenderedVehicles = transportService.getTenderedVehicles(searchTerm, year, month, pageable, user);
			return ResponseEntity.ok(tenderedVehicles);
		}
		catch(Exception e)
		{
			log.error("An error occurred while fetching ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching Tendered Vehicles");
		}
	}
	
	@GetMapping("/scrapped-vehicles")
	public ResponseEntity<?> getScrappedVehicles(
			@RequestParam(value="search", required = false) String searchTerm,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			Pageable pageable,
			@AuthenticationPrincipal User user
			)
	{
		try
		{
			Page<VehicleDraftListDTO> scrappedVehicles = transportService.getScrappedVehicles(searchTerm, year, month, pageable, user);
			return ResponseEntity.ok(scrappedVehicles);
		}
		catch(Exception e)
		{
			log.error("An error occurred while fetching ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching Scrapped Vehicles");
		}
	}
	
}
