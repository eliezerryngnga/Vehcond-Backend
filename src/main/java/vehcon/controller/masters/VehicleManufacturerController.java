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
import vehcon.dto.masters.VehicleManufacturerDTO;
import vehcon.exception.UnauthorizedException;
import vehcon.models.auth.User;
import vehcon.models.masters.VehicleManufacturer;
import vehcon.services.masters.VehicleManufacturerService;

@RestController
@RequestMapping("/vehicle-manufacturer")
@RequiredArgsConstructor
public class VehicleManufacturerController {
	
	
	private final VehicleManufacturerService vehicleManufacturerService;
	
	@GetMapping 
	public List<VehicleManufacturer> getVehicleManufacturer()
	{
		try
		{
			return vehicleManufacturerService.getVehicleManufacturer();
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
	public ResponseEntity<Page<VehicleManufacturer>> getAllVehicleManufacturer(
			@RequestParam(required = false) String searchTerm,
			Pageable pageable,
			@AuthenticationPrincipal User user)
	{
		try
		{
			Page<VehicleManufacturer> vehManufacturerPage = vehicleManufacturerService.getAllVehicleManufacturer(searchTerm, pageable);
			return ResponseEntity.ok(vehManufacturerPage);
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}
	
	@PostMapping("/add-vehicle-manufacturer")
	public ResponseEntity<String> addFinancialYear(@RequestBody VehicleManufacturerDTO dto)
	{
		try
		{
			VehicleManufacturer savedVehManufacturer = vehicleManufacturerService.addVehicleManufacturer(dto);
			
			return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully");
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to create");
		}
	}
}
