package vehcon.controller.masters;

import java.util.List;

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

import lombok.RequiredArgsConstructor;
import vehcon.dto.masters.VehiclePartDTO;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
import vehcon.models.masters.VehicleParts;
import vehcon.services.masters.VehiclePartsService;

@RestController
@RequestMapping("/vehicle-parts")
@RequiredArgsConstructor
public class VehiclePartsController {
	
	private final VehiclePartsService vehiclePartsService;
	
	@GetMapping
	List<VehicleParts> getByVehiclePartsName()
	{
		try
		{
			return vehiclePartsService.getByVehicleParts();
		}
		catch(UnauthorizedException ex)
		{
			throw ex;
		}
		catch(Exception e)
		{
			throw e;
		}	
	}	
	
	@GetMapping("/list")
	public ResponseEntity<Page<VehicleParts>> getAllFinancialYear(
			@RequestParam(required = false) String searchTerm,
			Pageable pageable,
			@AuthenticationPrincipal User user)
	{
		try
		{
			Page<VehicleParts> vehiclePartsPage = vehiclePartsService.getAllVehicleParts(searchTerm, pageable);
			return ResponseEntity.ok(vehiclePartsPage);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@PostMapping("/add-vehicle-parts")
	public ResponseEntity<String> addFinancialYear(@RequestBody VehiclePartDTO dto)
	{
		try
		{
			VehicleParts savedParts = vehiclePartsService.addVehicleParts(dto);
			
			return ResponseEntity.status(HttpStatus.CREATED).body("Vehicle Parts " + savedParts.getVehiclePartDescription() + " created successfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create vehicle parts: " + e.getMessage());
		}
	}
}
